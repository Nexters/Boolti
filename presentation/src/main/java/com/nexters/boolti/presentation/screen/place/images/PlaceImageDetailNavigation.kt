package com.nexters.boolti.presentation.screen.place.images

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.LocalNavController
import com.nexters.boolti.presentation.screen.navigation.MainRoute

fun NavGraphBuilder.placeImageDetailScreen(
    modifier: Modifier = Modifier,
) {
    composable<MainRoute.PlaceImageDetail> {
        val navController = LocalNavController.current

        PlaceImageDetailScreen(
            modifier = modifier,
            onBack = navController::popBackStack,
        )
    }
}
