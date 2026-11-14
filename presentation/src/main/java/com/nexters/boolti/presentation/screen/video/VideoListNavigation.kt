package com.nexters.boolti.presentation.screen.video

import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.nexters.boolti.presentation.screen.LocalNavController
import com.nexters.boolti.presentation.screen.navigation.VideoListRoute
import com.nexters.boolti.presentation.util.ObserveNavResult

fun NavGraphBuilder.videoListScreen(
    modifier: Modifier = Modifier,
) {
    composable<VideoListRoute.VideoList> { entry ->
        val navController = LocalNavController.current
        val viewModel: VideoListViewModel = hiltViewModel()
        ObserveNavResult<VideoEditResult>(entry.savedStateHandle, VideoEditResult.KEY) {
            viewModel.onAction(VideoListAction.EditResultReceived(it))
        }
        VideoListScreen(
            modifier = modifier,
            viewModel = viewModel,
            navigateUp = navController::navigateUp,
            navigateToAddVideo = { closeListOnBack ->
                navController.navigate(VideoListRoute.VideoEdit(closeListOnBack = closeListOnBack))
            },
            navigateToEditVideo = { video ->
                navController.navigate(VideoListRoute.VideoEdit(localId = video.localId, url = video.url))
            },
        )
    }
}

fun NavGraphBuilder.videoEditScreen(
    modifier: Modifier = Modifier,
) {
    composable<VideoListRoute.VideoEdit> {
        val navController = LocalNavController.current
        VideoEditScreen(
            modifier = modifier,
            navigateUp = navController::navigateUp,
            closeList = { navController.popBackStack<VideoListRoute.VideoList>(inclusive = true) },
            returnResult = { result ->
                navController.previousBackStackEntry?.savedStateHandle?.set(VideoEditResult.KEY, result)
                navController.navigateUp()
            },
        )
    }
}
