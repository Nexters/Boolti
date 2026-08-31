package com.nexters.boolti.presentation.screen.home

import com.nexters.boolti.presentation.screen.navigation.HomeRoute

sealed interface HomeEvent {
    data class NavigateToHomeRoute(
        val route: HomeRoute,
    ) : HomeEvent

    data class GiftNotification(
        val status: GiftStatus
    ) : HomeEvent

    data object GiftRegistered : HomeEvent

    data class NavigateToGiftPreQuestion(
        val giftUuid: String,
        val showId: String,
    ) : HomeEvent
}
