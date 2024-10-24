package com.nexters.boolti.presentation.reservationdetail

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.MainDestination

fun NavGraphBuilder.reservationDetailScreen(
    navController: NavHostController,
    navigateTo: (String) -> Unit,
    popBackStack: () -> Unit,
) {
    composable(
        route = MainDestination.ReservationDetail.route,
        arguments = MainDestination.ReservationDetail.arguments,
    ) {
        ReservationDetailScreen(
            onBackPressed = popBackStack,
            navigateToRefund = { id, isGift ->
                navigateTo(MainDestination.Refund.createRoute(id = id, isGift = isGift))
            },
        )
    }
}
