package com.nexters.boolti.presentation.screen.business

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.LocalNavController
import com.nexters.boolti.presentation.screen.navigation.MainRoute

fun NavGraphBuilder.businessScreen() {
    composable<MainRoute.Business> {
        val navController = LocalNavController.current
        BusinessScreen(
            onBackPressed = navController::popBackStack
        )
    }
}
