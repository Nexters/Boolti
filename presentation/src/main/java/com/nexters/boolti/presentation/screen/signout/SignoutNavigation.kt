package com.nexters.boolti.presentation.screen.signout

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.extension.navigateToHome
import com.nexters.boolti.presentation.screen.LocalNavController
import com.nexters.boolti.presentation.screen.navigation.MainRoute

fun NavGraphBuilder.signoutScreen() {
    composable<MainRoute.SignOut> {
        val navController = LocalNavController.current
        SignoutScreen(
            navigateToHome = navController::navigateToHome,
            navigateBack = navController::popBackStack,
        )
    }
}
