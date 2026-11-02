package com.nexters.boolti.presentation.screen.video

import com.nexters.boolti.domain.model.YouTubeVideo

data class VideoListUiState(
    val videos: List<YouTubeVideo> = emptyList(),
    val originalVideos: List<YouTubeVideo> = emptyList(),
    val editing: Boolean = false,
    val saving: Boolean = false,
    val loading: Boolean = false,
    val isMine: Boolean = false,
    val showExitAlertDialog: Boolean = false,
) {
    val edited: Boolean
        get() = videos != originalVideos

    val saveEnabled: Boolean
        get() = !saving && edited
}

sealed interface VideoListAction {
    data object Back : VideoListAction
    data object Exit : VideoListAction
    data object Save : VideoListAction
    data object StartEditing : VideoListAction
    data object DismissExitAlertDialog : VideoListAction
    data class Reorder(val from: Int, val to: Int) : VideoListAction
    data object ClickAddVideo : VideoListAction
    data class ClickVideo(val localId: String) : VideoListAction
    data class EditResultReceived(val result: VideoEditResult) : VideoListAction
}

sealed interface VideoListEvent {
    data object Added : VideoListEvent
    data object Edited : VideoListEvent
    data object Removed : VideoListEvent
    data class NavigateToAddVideo(val closeListOnBack: Boolean) : VideoListEvent
    data class NavigateToEditVideo(val video: YouTubeVideo) : VideoListEvent
    data object Finish : VideoListEvent
}
