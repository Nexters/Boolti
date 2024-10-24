package com.nexters.boolti.presentation.screen.giftcomplete

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.MainDestination

fun NavGraphBuilder.giftCompleteScreen(
    navigateTo: (String) -> Unit,
    navigateToHome: () -> Unit,
    popBackStack: () -> Unit,
) {
    composable(
        route = MainDestination.GiftComplete.route,
    ) {
        GiftCompleteScreen(
            onClickClose = popBackStack,
            onClickHome = navigateToHome,
            navigateToReservation = { reservation ->
                navigateTo(
                    MainDestination.ReservationDetail.createRoute(
                        id = reservation.id,
                        isGift = true,
                    )
                )
            },
        )
    }
}