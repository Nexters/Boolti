package com.nexters.boolti.presentation.screen.signout

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.MainDestination

fun NavGraphBuilder.SignoutScreen(
    navigateToHome: () -> Unit,
    popBackStack: () -> Unit,
) {
    composable(
        route = MainDestination.SignOut.route,
    ) {
        SignoutScreen(
            navigateToHome = navigateToHome,
            navigateBack = popBackStack,
        )
    }
}
