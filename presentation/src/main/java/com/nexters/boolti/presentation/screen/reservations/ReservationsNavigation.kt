package com.nexters.boolti.presentation.screen.reservations

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.MainDestination
import com.nexters.boolti.presentation.screen.navigation.MainRoute

fun NavGraphBuilder.reservationsScreen(
    navController: NavHostController,
    navigateTo: (String) -> Unit,
    popBackStack: () -> Unit,
) {
    composable<MainRoute.Reservations> {
        ReservationsScreen(
            onBackPressed = popBackStack,
            navigateToDetail = { id, isGift ->
                navigateTo(MainDestination.ReservationDetail.createRoute(id = id, isGift = isGift))
            })
    }
}
