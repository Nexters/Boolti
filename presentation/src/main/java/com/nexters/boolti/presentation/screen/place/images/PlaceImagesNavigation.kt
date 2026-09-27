package com.nexters.boolti.presentation.screen.place.images

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.nexters.boolti.presentation.screen.LocalNavController
import com.nexters.boolti.presentation.screen.navigation.MainRoute

fun NavGraphBuilder.placeImagesScreen(
    modifier: Modifier = Modifier,
) {
    composable<MainRoute.PlaceImages> { entry ->
        val navController = LocalNavController.current
        val route = entry.toRoute<MainRoute.PlaceImages>()

        PlaceImagesScreen(
            modifier = modifier,
            onBack = navController::popBackStack,
            onClickImage = { imageId ->
                navController.navigate(
                    MainRoute.PlaceImageDetail(
                        placeId = route.placeId,
                        initialImageId = imageId
                    ),
                )
            },
        )
    }
}
