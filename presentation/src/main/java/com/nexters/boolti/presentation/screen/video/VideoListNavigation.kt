package com.nexters.boolti.presentation.screen.video

import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.nexters.boolti.presentation.screen.LocalBackStack
import com.nexters.boolti.presentation.screen.navigation.VideoListRoute
import com.nexters.boolti.presentation.screen.navigation.decorator.SharedViewModelStoreNavEntryDecorator

private val videoListContentKey: String = VideoListRoute.VideoList::class.qualifiedName ?: "VideoEdit"

fun EntryProviderScope<NavKey>.videoListScreen(
    modifier: Modifier = Modifier,
) {
    entry<VideoListRoute.VideoList>(
        clazzContentKey = { videoListContentKey },
    ) { key ->
        val viewModel = hiltViewModel<VideoListViewModel, VideoListViewModel.Factory>(
            creationCallback = { factory ->
                factory.create(key)
            }
        )
        val backStack = LocalBackStack.current

        VideoListScreen(
            modifier = modifier,
            navigateToAddVideo = {
                backStack.add(VideoListRoute.VideoEdit(false))
            },
            navigateToEditVideo = {
                backStack.add(VideoListRoute.VideoEdit(true))
            },
            navigateUp = backStack::removeLastOrNull,
            viewModel = viewModel,
        )
    }

    entry<VideoListRoute.VideoEdit>(
        metadata =
            SharedViewModelStoreNavEntryDecorator.parent(videoListContentKey),
    ) {
        val backStack = LocalBackStack.current
        VideoEditScreen(
            modifier = modifier,
            navigateUp = backStack::removeLastOrNull,
        )
    }
}
