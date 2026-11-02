package com.nexters.boolti.presentation.screen.video

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class VideoEditUiState(
    val isEditMode: Boolean = false,
    val url: String = "",
    val originalUrl: String = "",
) {
    val completeEnabled: Boolean
        get() = url.isNotBlank() && url != originalUrl
}

sealed interface VideoEditAction {
    data object Back : VideoEditAction
    data class ChangeUrl(val url: String) : VideoEditAction
    data object Complete : VideoEditAction
    data object Remove : VideoEditAction
}

sealed interface VideoEditEvent {
    data class Done(val result: VideoEditResult) : VideoEditEvent
    data object Close : VideoEditEvent
    data object CloseWithList : VideoEditEvent
}

/** 편집 화면이 목록 화면에 돌려주는 결과. 목록 화면 SavedStateHandle의 [KEY]로 전달한다 */
sealed interface VideoEditResult : Parcelable {
    @Parcelize
    data class Added(val url: String) : VideoEditResult

    @Parcelize
    data class Edited(val localId: String, val url: String) : VideoEditResult

    @Parcelize
    data class Removed(val localId: String) : VideoEditResult

    companion object {
        const val KEY = "video_edit_result"
    }
}
