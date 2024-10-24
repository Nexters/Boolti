package com.nexters.boolti.presentation.screen.reservations

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.MainDestination

fun NavGraphBuilder.reservationsScreen(
    navController: NavHostController,
    navigateTo: (String) -> Unit,
    popBackStack: () -> Unit,
) {
    composable(
        route = MainDestination.Reservations.route,
    ) {
        ReservationsScreen(
            onBackPressed = popBackStack,
            navigateToDetail = { id, isGift ->
                navigateTo(MainDestination.ReservationDetail.createRoute(id = id, isGift = isGift))
            })
    }
}
