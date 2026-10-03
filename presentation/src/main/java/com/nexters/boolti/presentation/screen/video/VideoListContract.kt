package com.nexters.boolti.presentation.screen.video

import com.nexters.boolti.domain.model.YouTubeVideo

data class VideoListUiState(
    val videos: List<YouTubeVideo> = emptyList(),
    val originalVideos: List<YouTubeVideo> = emptyList(),
    val editingVideo: YouTubeVideo? = null,
    val editingVideoOriginalUrl: String? = null,
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

    val editingVideoCompleteEnabled: Boolean
        get() = editingVideoOriginalUrl != null && editingVideo != null &&
                editingVideo.url.isNotBlank() && editingVideoOriginalUrl != editingVideo.url
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
    data class ChangeVideoUrl(val url: String) : VideoListAction
    data object CompleteVideo : VideoListAction
    data object RemoveVideo : VideoListAction
}

/** 목록·편집 화면이 같은 Event를 나눠 받으므로, 어느 화면이 받아도 결과가 같게 처리한다 */
sealed interface VideoListEvent {
    data object Added : VideoListEvent
    data object Edited : VideoListEvent
    data object Removed : VideoListEvent
    data class NavigateToEdit(val isEditMode: Boolean) : VideoListEvent
    data object CloseEdit : VideoListEvent
    data object Finish : VideoListEvent
}
