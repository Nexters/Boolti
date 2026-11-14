package com.nexters.boolti.presentation.screen.profileedit.link

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class LinkEditUiState(
    val isEditMode: Boolean = false,
    val name: String = "",
    val url: String = "",
) {
    val completeEnabled: Boolean
        get() = name.isNotBlank() && url.isNotBlank()
}

sealed interface LinkEditAction {
    data object Back : LinkEditAction
    data class ChangeName(val name: String) : LinkEditAction
    data class ChangeUrl(val url: String) : LinkEditAction
    data object Complete : LinkEditAction
    data object Remove : LinkEditAction
}

sealed interface LinkEditEvent {
    data class Done(val result: LinkEditResult) : LinkEditEvent
    data object Close : LinkEditEvent
    data object CloseWithList : LinkEditEvent
}

/** 편집 화면이 목록 화면에 돌려주는 결과. 목록 화면 SavedStateHandle의 [KEY]로 전달한다 */
sealed interface LinkEditResult : Parcelable {
    @Parcelize
    data class Added(val name: String, val url: String) : LinkEditResult

    @Parcelize
    data class Edited(val id: String, val name: String, val url: String) : LinkEditResult

    @Parcelize
    data class Removed(val id: String) : LinkEditResult

    companion object {
        const val KEY = "link_edit_result"
    }
}
