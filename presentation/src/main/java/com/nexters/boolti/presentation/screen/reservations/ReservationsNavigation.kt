package com.nexters.boolti.presentation.screen.reservations

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.MainDestination
import com.nexters.boolti.presentation.screen.reservationId

fun NavGraphBuilder.ReservationsScreen(
    navigateTo: (String) -> Unit,
    popBackStack: () -> Unit,
) {
    composable(
        route = MainDestination.Reservations.route,
    ) {
        ReservationsScreen(
            onBackPressed = popBackStack,
            navigateToDetail = { reservationId ->
                navigateTo("${MainDestination.Reservations.route}/$reservationId")
            })
    }
}
