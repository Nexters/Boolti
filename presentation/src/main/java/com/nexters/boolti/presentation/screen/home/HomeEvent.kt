package com.nexters.boolti.presentation.screen.home

sealed interface HomeEvent {
    data class DeepLinkEvent(
        val deepLink: String,
    ) : HomeEvent

    data class ShowMessage(
        val status: GiftStatus
    ) : HomeEvent
}