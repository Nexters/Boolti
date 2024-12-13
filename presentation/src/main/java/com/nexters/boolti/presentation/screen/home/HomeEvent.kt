package com.nexters.boolti.presentation.screen.home

sealed interface HomeEvent {
    data class DeepLinkEvent(
        val deepLink: String,
    ) : HomeEvent

    data class GiftNotification(
        val status: GiftStatus
    ) : HomeEvent

    data object GiftRegistered : HomeEvent
}