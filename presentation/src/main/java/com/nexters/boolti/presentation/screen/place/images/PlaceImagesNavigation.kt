package com.nexters.boolti.presentation.screen.place.images

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.LocalNavController
import com.nexters.boolti.presentation.screen.navigation.MainRoute

fun NavGraphBuilder.placeImagesScreen(
    modifier: Modifier = Modifier,
) {
    composable<MainRoute.PlaceImages> {
        val navController = LocalNavController.current

        PlaceImagesScreen(
            modifier = modifier,
            onBack = navController::popBackStack,
            // TODO(Phase 4): 사진 크게 보기 화면으로 이동
            onClickImage = {},
        )
    }
}
