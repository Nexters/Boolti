package com.nexters.boolti.presentation.screen.place

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.extension.navigateToHome
import com.nexters.boolti.presentation.screen.LocalNavController
import com.nexters.boolti.presentation.screen.navigation.MainRoute

fun NavGraphBuilder.placeScreen(
    modifier: Modifier = Modifier,
) {
    composable<MainRoute.Place> {
        val navController = LocalNavController.current
        PlaceScreen(
            modifier = modifier,
            onBack = navController::popBackStack,
            navigateTo = { route -> navController.navigate(route) },
            navigateToHome = navController::navigateToHome,
        )
    }
}
