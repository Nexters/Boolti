package com.nexters.boolti.presentation.screen.refund

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.nexters.boolti.presentation.screen.LocalNavController
import com.nexters.boolti.presentation.screen.navigation.MainRoute

fun NavGraphBuilder.refundScreen() {
    composable<MainRoute.Refund> { entry ->
        val navController = LocalNavController.current
        val route = entry.toRoute<MainRoute.Refund>()

        RefundScreen(
            isGift = route.isGift,
            onBackPressed = navController::popBackStack,
        )
    }
}

fun EntryProviderScope<NavKey>.refundScreen() {
    entry<MainRoute.Refund> { entry ->
        val navController = LocalNavController.current

        RefundScreen(
            isGift = entry.isGift,
            onBackPressed = navController::popBackStack,
        )
    }
}
