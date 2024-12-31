package com.nexters.boolti.presentation.screen.reservations

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.LocalNavController
import com.nexters.boolti.presentation.screen.navigation.MainRoute

fun NavGraphBuilder.reservationsScreen() {
    composable<MainRoute.Reservations> {
        val navController = LocalNavController.current
        ReservationsScreen(
            onBackPressed = navController::popBackStack,
            navigateToDetail = { id, isGift ->
                navController.navigate(
                    MainRoute.ReservationDetail(
                        reservationId = id,
                        isGift = isGift
                    )
                )
            },
        )
    }
}
