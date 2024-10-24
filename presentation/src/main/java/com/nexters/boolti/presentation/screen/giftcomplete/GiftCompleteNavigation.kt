package com.nexters.boolti.presentation.screen.giftcomplete

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.MainDestination
import com.nexters.boolti.presentation.screen.navigation.MainRoute

fun NavGraphBuilder.giftCompleteScreen(
    navController: NavHostController,
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
                navController.navigate(
                    MainRoute.ReservationDetail(
                        reservationId = reservation.id,
                        isGift = true,
                    )
                )
            },
        )
    }
}