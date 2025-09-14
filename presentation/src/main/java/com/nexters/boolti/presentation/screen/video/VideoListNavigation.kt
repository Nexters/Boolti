package com.nexters.boolti.presentation.screen.video

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.LocalNavController
import com.nexters.boolti.presentation.screen.navigation.VideoListRoute

fun NavGraphBuilder.videoListScreen(
    modifier: Modifier = Modifier,
    getSharedViewModel: @Composable (NavBackStackEntry) -> VideoListViewModel,
) {
    composable<VideoListRoute.VideoList> { entry ->
        val navController = LocalNavController.current
        VideoListScreen(
            modifier = modifier,
            navigateUp = navController::navigateUp,
            navigateToAddVideo = {
                navController.navigate(VideoListRoute.VideoEdit(false))
            },
            navigateToEditVideo = {
                navController.navigate(VideoListRoute.VideoEdit(true))
            },
            viewModel = getSharedViewModel(entry),
        )
    }
}

fun NavGraphBuilder.videoEditScreen(
    modifier: Modifier = Modifier,
    getSharedViewModel: @Composable (NavBackStackEntry) -> VideoListViewModel,
) {
    composable<VideoListRoute.VideoEdit> { entry ->
        val navController = LocalNavController.current
        VideoEditScreen(
            modifier = modifier,
            navigateUp = navController::navigateUp,
            viewModel = getSharedViewModel(entry),
        )
    }
}
