package com.nexters.boolti.presentation.screen.home

import androidx.lifecycle.viewModelScope
import com.nexters.boolti.common.tracker.AppTracker
import com.nexters.boolti.common.tracker.event.complete
import com.nexters.boolti.domain.repository.AuthRepository
import com.nexters.boolti.domain.repository.GiftRepository
import com.nexters.boolti.domain.repository.TicketingRepository
import com.nexters.boolti.presentation.base.BaseViewModel
import com.nexters.boolti.presentation.screen.HomeNavigationEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val giftRepository: GiftRepository,
    private val homeNavigationEvent: HomeNavigationEvent,
    private val ticketingRepository: TicketingRepository,
) : BaseViewModel() {
    val loggedIn = authRepository.loggedIn.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        null,
    )

    private val _events = Channel<HomeEvent>(Channel.BUFFERED)
    val events: Flow<HomeEvent> = _events.receiveAsFlow()

    /**
     * 1. 딥 링크를 통해 앱이 실행되면 아래 메서드를 호출하여 [pendingGift]를 초기화한다. (오버로딩에 유의)
     * @see [processGift(giftUuid: String)][processGift]
     * 2. 로그인 되어 있을 경우 [processGiftWhenLoggedIn]를 호출하여 등록 다이얼로그를 띄운다.
     * 3. [receiveGift]를 호출하여 등록을 완료한다.
     */
    private var pendingGift: PendingGift? = null

    init {
        fetchUserInfo()
        sendFcmToken()
        collectHomeNavigationEvent()
    }

    private fun fetchUserInfo() {
        authRepository.getUserAndCache()
            .launchIn(viewModelScope + recordExceptionHandler)
    }

    private fun sendFcmToken() {
        viewModelScope.launch {
            loggedIn.collectLatest {
                if (it == true) authRepository.sendFcmToken()
            }
        }
    }

    private fun collectHomeNavigationEvent() {
        homeNavigationEvent.events
            .onEach { sendEvent(HomeEvent.NavigateToHomeRoute(it)) }
            .launchIn(viewModelScope)
    }

    fun processGift() {
        val gift = pendingGift
        if (gift is PendingGift.Unprocessed) {
            processGiftWhenLoggedIn(gift.giftUuid)
        }
    }

    fun processGift(giftUuid: String) {
        viewModelScope.launch {
            // 딥링크를 통해 cold start가 발생할 경우 loggedIn의 초기값인 null이 들어오는데, null 대신 로그인 정보를 가져오는 걸 기다리기 위함
            val isLoggedIn = loggedIn.filterNotNull().first()
            if (isLoggedIn) {
                processGiftWhenLoggedIn(giftUuid)
            } else {
                pendingGift = PendingGift.Unprocessed(giftUuid)
                sendEvent(HomeEvent.GiftNotification(GiftStatus.NEED_LOGIN))
            }
        }
    }

    private fun processGiftWhenLoggedIn(giftUuid: String) {
        viewModelScope.launch(recordExceptionHandler) {
            val gift = giftRepository
                .getGift(giftUuid)
                .first()
            val senderId = gift.senderUserId
            val hasPreQuestion =
                ticketingRepository.getPreQuestions(gift.showId).first().isNotEmpty()
            val myUserId = authRepository.cachedUser.first()?.id ?: return@launch

            pendingGift = PendingGift.Ready(
                giftUuid = gift.uuid,
                showId = gift.showId,
            )

            if (hasPreQuestion) {
                sendEvent(HomeEvent.NavigateToGiftPreQuestion(gift.uuid, gift.showId))
                return@launch
            }

            if (senderId == myUserId) {
                sendEvent(HomeEvent.GiftNotification(GiftStatus.SELF))
            } else {
                sendEvent(HomeEvent.GiftNotification(GiftStatus.CAN_REGISTER))
            }
        }
    }

    private fun sendEvent(event: HomeEvent) {
        viewModelScope.launch {
            _events.trySend(event)
        }
    }

    fun receiveGift() {
        val ready = pendingGift as? PendingGift.Ready ?: return
        val giftUuid = ready.giftUuid

        pendingGift = null

        giftRepository.receiveGift(giftUuid)
            .onEach { isSuccessful ->
                if (isSuccessful) {
                    AppTracker.complete(
                        target = "GiftRegistration",
                        properties = mapOf(
                            "gift_id" to giftUuid,
                            "show_id" to ready.showId,
                        ),
                    )
                    sendEvent(HomeEvent.GiftRegistered)
                } else {
                    sendEvent(HomeEvent.GiftNotification(GiftStatus.FAILED))
                }
            }
            .launchIn(viewModelScope + recordExceptionHandler)
    }

    fun cancelGift() {
        pendingGift = null
    }
}
