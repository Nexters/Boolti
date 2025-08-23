package com.nexters.boolti.presentation.screen.video

import com.nexters.boolti.domain.model.YouTubeVideo

data class VideoListState(
    val videos: List<YouTubeVideo> = emptyList(),
    val originalVideos: List<YouTubeVideo> = emptyList(),
    val editingVideo: YouTubeVideo? = null,
    val editing: Boolean = false,
    val saving: Boolean = false,
    val isMine: Boolean = false,
    val showExitAlertDialog: Boolean = false,
) {
    val edited: Boolean
        get() = videos != originalVideos

    val saveEnabled: Boolean
        get() = !saving && edited
}