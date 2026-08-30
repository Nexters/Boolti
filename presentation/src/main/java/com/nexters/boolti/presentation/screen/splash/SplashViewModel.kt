package com.nexters.boolti.presentation.screen.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.repository.ConfigRepository
import com.nexters.boolti.presentation.screen.HomeNavigationEvent
import com.nexters.boolti.presentation.screen.navigation.HomeRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    configRepository: ConfigRepository,
    private val homeNavigationEvent: HomeNavigationEvent,
) : ViewModel() {
    init {
        viewModelScope.launch {
            configRepository.cacheRefundPolicy()
        }
    }

    val shouldUpdate = configRepository.shouldUpdate()

    fun navigateToHome(route: HomeRoute) {
        viewModelScope.launch {
            homeNavigationEvent.sendEvent(route)
        }
    }
}
