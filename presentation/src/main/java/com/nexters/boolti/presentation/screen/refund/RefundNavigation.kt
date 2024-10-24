package com.nexters.boolti.presentation.screen.refund

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.nexters.boolti.presentation.screen.MainDestination
import com.nexters.boolti.presentation.screen.navigation.MainRoute

fun NavGraphBuilder.refundScreen(
    navController: NavHostController,
) {
    composable<MainRoute.Refund> { entry ->
        val route = entry.toRoute<MainRoute.Refund>()

        RefundScreen(
            isGift = route.isGift,
            onBackPressed = navController::popBackStack,
        )
    }
}
