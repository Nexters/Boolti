package com.nexters.boolti.presentation.screen.signout

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.extension.navigateToHome
import com.nexters.boolti.presentation.screen.MainDestination
import com.nexters.boolti.presentation.screen.navigation.MainRoute

fun NavGraphBuilder.signoutScreen(
    navController: NavHostController,
) {
    composable<MainRoute.SignOut> {
        SignoutScreen(
            navigateToHome = navController::navigateToHome,
            navigateBack = navController::popBackStack,
        )
    }
}
