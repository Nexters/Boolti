package com.nexters.boolti.presentation.screen.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.repository.ConfigRepository
import com.nexters.boolti.presentation.screen.DeepLinkEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    configRepository: ConfigRepository,
    private val deepLinkEvent: DeepLinkEvent,
) : ViewModel() {
    init {
        viewModelScope.launch {
            configRepository.cacheRefundPolicy()
        }
    }

    val shouldUpdate = configRepository.shouldUpdate()

    fun sendDeepLinkEvent(deeplink: String) {
        viewModelScope.launch {
            deepLinkEvent.sendEvent(deeplink)
        }
    }
}

