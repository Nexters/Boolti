package com.nexters.boolti.presentation.screen.home

import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.repository.AuthRepository
import com.nexters.boolti.domain.repository.GiftRepository
import com.nexters.boolti.presentation.base.BaseViewModel
import com.nexters.boolti.presentation.screen.DeepLinkEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
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
) : BaseViewModel() {
    val loggedIn = authRepository.loggedIn.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null,
    )

    private val _events = MutableSharedFlow<HomeEvent>()
    val events: SharedFlow<HomeEvent> = _events.asSharedFlow()

    init {
        initUserInfo()
        sendFcmToken()
        collectDeepLinkEvent()
    }

    private fun initUserInfo() {
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

    fun receiveGift(giftUuid: String) {
        when (loggedIn.value) {
            true -> {
                sendEvent(HomeEvent.ShowMessage(GiftStatus.CAN_REGISTER))
            }

            false -> {
                giftRepository.saveGift(giftUuid)
                sendEvent(HomeEvent.ShowMessage(GiftStatus.NEED_LOGIN))
            }

            null -> TODO()
        }
    }

    private fun sendEvent(event: HomeEvent) {
        viewModelScope.launch {
            _events.emit(event)
        }
    }
}
