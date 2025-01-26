package com.nexters.boolti.presentation.screen.reservationdetail

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.LocalNavController
import com.nexters.boolti.presentation.screen.navigation.MainRoute

fun NavGraphBuilder.reservationDetailScreen() {
    composable<MainRoute.ReservationDetail> {
        val navController = LocalNavController.current
        ReservationDetailScreen(
            onBackPressed = navController::popBackStack,
            navigateToRefund = { id, isGift ->
                navController.navigate(MainRoute.Refund(reservationId = id, isGift = isGift))
            },
        )
    }
}
