package com.nexters.boolti.presentation.screen.signout

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.MainDestination
import com.nexters.boolti.presentation.screen.navigation.MainRoute

fun NavGraphBuilder.signoutScreen(
    navController: NavHostController,
    navigateToHome: () -> Unit,
    popBackStack: () -> Unit,
) {
    composable<MainRoute.SignOut> {
        SignoutScreen(
            navigateToHome = navigateToHome,
            navigateBack = popBackStack,
        )
    }
}
