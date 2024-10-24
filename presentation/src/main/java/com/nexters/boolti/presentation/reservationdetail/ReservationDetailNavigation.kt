package com.nexters.boolti.presentation.reservationdetail

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.navigation.MainRoute

fun NavGraphBuilder.reservationDetailScreen(
    navController: NavHostController,
    popBackStack: () -> Unit,
) {
    composable<MainRoute.ReservationDetail> {
        ReservationDetailScreen(
            onBackPressed = popBackStack,
            navigateToRefund = { id, isGift ->
                navController.navigate(MainRoute.Refund(reservationId = id, isGift = isGift))
            },
        )
    }
}
