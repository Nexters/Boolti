package com.nexters.boolti.presentation.screen.business

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.navigation.MainRoute

fun NavGraphBuilder.businessScreen(
    navController: NavHostController,
) {
    composable<MainRoute.Business> {
        BusinessScreen(
            onBackPressed = navController::popBackStack
        )
    }
}