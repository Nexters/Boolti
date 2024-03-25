package com.nexters.boolti.presentation.screen.home

sealed interface HomeEvent {
    data class NavigateToDeepLink(
        val deeplink: String
    ) : HomeEvent
}
