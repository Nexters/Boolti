package com.nexters.boolti.presentation.screen.reservations

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.nexters.boolti.presentation.screen.LocalBackStack
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

fun EntryProviderScope<NavKey>.reservationsScreen() {
    entry<MainRoute.Reservations> { key ->
        val backStack = LocalBackStack.current
        ReservationsScreen(
            onBackPressed = backStack::removeLastOrNull,
            navigateToDetail = { id, isGift ->
                backStack.add(
                    MainRoute.ReservationDetail(
                        reservationId = id,
                        isGift = isGift
                    )
                )
            },
        )
    }
}
