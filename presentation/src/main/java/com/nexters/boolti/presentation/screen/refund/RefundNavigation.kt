package com.nexters.boolti.presentation.screen.refund

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
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
