package com.nexters.boolti.presentation.reservationdetail

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.MainDestination

fun NavGraphBuilder.ReservationDetailScreen(
    navigateTo: (String) -> Unit,
    popBackStack: () -> Unit,
) {
    composable(
        route = MainDestination.ReservationDetail.route,
        arguments = MainDestination.ReservationDetail.arguments,
    ) {
        ReservationDetailScreen(
            onBackPressed = popBackStack,
            navigateToRefund = { id -> navigateTo("${MainDestination.Refund.route}/$id") },
        )
    }
}
