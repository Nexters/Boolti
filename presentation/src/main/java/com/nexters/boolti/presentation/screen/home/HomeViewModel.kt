package com.nexters.boolti.presentation.screen.home

import androidx.lifecycle.viewModelScope
import com.nexters.boolti.common.tracker.AppTracker
import com.nexters.boolti.common.tracker.event.complete
import com.nexters.boolti.domain.repository.AuthRepository
import com.nexters.boolti.domain.repository.GiftRepository
import com.nexters.boolti.domain.repository.ReservationRepository
import com.nexters.boolti.domain.repository.TicketingRepository
import com.nexters.boolti.presentation.base.BaseViewModel
import com.nexters.boolti.presentation.screen.DeepLinkEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val giftRepository: GiftRepository,
    private val deepLinkEvent: DeepLinkEvent,
    private val reservationRepository: ReservationRepository,
    private val ticketingRepository: TicketingRepository,
) : BaseViewModel() {
    val loggedIn = authRepository.loggedIn.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        null,
    )

    private val _events = MutableSharedFlow<HomeEvent>()
    val events: SharedFlow<HomeEvent> = _events.asSharedFlow()

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
        collectDeepLinkEvent()
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

    private fun collectDeepLinkEvent() {
        deepLinkEvent.events
            .filter { it.startsWith("https://app.boolti.in/home") }
            .onEach { sendEvent(HomeEvent.DeepLinkEvent(it)) }
            .launchIn(viewModelScope)
    }

    fun processGift() {
        val gift = pendingGift
        if (gift is PendingGift.Unprocessed) {
            processGiftWhenLoggedIn(gift.giftUuid)
        }
    }

    fun processGift(giftUuid: String) {
        pendingGift = PendingGift.Unprocessed(giftUuid)

        when (loggedIn.value) {
            true -> processGiftWhenLoggedIn(giftUuid)
            false -> sendEvent(HomeEvent.GiftNotification(GiftStatus.NEED_LOGIN))
            null -> sendEvent(HomeEvent.GiftNotification(GiftStatus.FAILED)) // 예기치 못한 오류... FIXME: cold start와 함께 선물을 받았을 때 이 경로를 탐. 아마 HomeScreen에 의해 Lazy를 바꾸면 해결될까?
        }
    }

    private fun processGiftWhenLoggedIn(giftUuid: String) {
        viewModelScope.launch(recordExceptionHandler) {
            val gift = giftRepository
                .getGift(giftUuid)
                .first()
            val senderId = gift.senderUserId

            val hasPreQuestion = ticketingRepository.getPreQuestions(gift.showId).first().isNotEmpty()

            // TODO: 계획
            // 3. 사전질문이 있을 경우 등록하기 버튼은 다음 화면으로 가는 버튼이 된다.
            // 4. 다음 화면에서 사전 질문을 작성한 뒤 완료하면 두 가지 api 호출
            reservationRepository.getPreQuestionAnswers(gift.reservationId).first()
            reservationRepository.findReservationById(gift.reservationId).first()

            val myUserId = authRepository.cachedUser.first()?.id ?: return@launch

            pendingGift = PendingGift.Ready(
                giftUuid = gift.uuid,
                showId = gift.showId,
                hasPreQuestion = hasPreQuestion,
            )
            if (senderId == myUserId) {
                sendEvent(HomeEvent.GiftNotification(GiftStatus.SELF))
            } else {
                sendEvent(HomeEvent.GiftNotification(GiftStatus.CAN_REGISTER))
            }
        }
    }

    private fun sendEvent(event: HomeEvent) {
        viewModelScope.launch {
            _events.emit(event)
        }
    }

    fun receiveGift() {
        val ready = pendingGift as? PendingGift.Ready ?: return
        val giftUuid = ready.giftUuid

        if (ready.hasPreQuestion) {
            sendEvent(HomeEvent.NavigateToGiftPreQuestion(giftUuid, ready.showId))
            return
        }

        pendingGift = null

        giftRepository.receiveGift(giftUuid)
            .onEach { isSuccessful ->
                if (isSuccessful) {
                    AppTracker.complete(
                        target = "GiftRegistration",
                        properties = mapOf(
                            "gift_id" to giftUuid,
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
