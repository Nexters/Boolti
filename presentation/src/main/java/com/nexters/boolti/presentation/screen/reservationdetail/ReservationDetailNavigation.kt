package com.nexters.boolti.presentation.screen.reservationdetail

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.nexters.boolti.presentation.screen.LocalBackStack
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

fun EntryProviderScope<NavKey>.reservationDetailScreen() {
    entry<MainRoute.ReservationDetail> { key ->
        val viewModel = hiltViewModel<ReservationDetailViewModel, ReservationDetailViewModel.Factory>(
            creationCallback = { factory ->
                factory.create(key)
            }
        )

        val backStack = LocalBackStack.current
        ReservationDetailScreen(
            onBackPressed = backStack::removeLastOrNull,
            navigateToRefund = { id, isGift ->
                backStack.add(MainRoute.Refund(reservationId = id, isGift = isGift))
            },
            viewModel = viewModel,
        )
    }
}
