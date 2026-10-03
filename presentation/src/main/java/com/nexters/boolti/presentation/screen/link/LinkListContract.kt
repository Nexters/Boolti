package com.nexters.boolti.presentation.screen.link

import com.nexters.boolti.domain.model.Link

data class LinkListUiState(
    val isMine: Boolean = false,
    val editing: Boolean = false,
    val originalLinks: List<Link> = emptyList(),
    val links: List<Link> = emptyList(),
    val editingLink: Link? = null,
    val saving: Boolean = false,
    val showExitAlertDialog: Boolean = false,
) {
    val edited: Boolean = links != originalLinks
    val saveEnabled: Boolean = edited && !saving
}

sealed interface LinkListAction {
    data object Back : LinkListAction
    data object Exit : LinkListAction
    data object Save : LinkListAction
    data object StartEditing : LinkListAction
    data object DismissExitAlertDialog : LinkListAction
    data class Reorder(val from: Int, val to: Int) : LinkListAction

    data object ClickAddLink : LinkListAction
    data class ClickLink(val linkId: String) : LinkListAction
    data class ChangeLinkName(val name: String) : LinkListAction
    data class ChangeLinkUrl(val url: String) : LinkListAction
    data object CompleteLink : LinkListAction
    data object RemoveLink : LinkListAction
}

/** 목록·편집 화면이 같은 Event를 나눠 받으므로, 어느 화면이 받아도 결과가 같게 처리한다 */
sealed interface LinkListEvent {
    data object Added : LinkListEvent
    data object Edited : LinkListEvent
    data object Removed : LinkListEvent
    data class NavigateToEdit(val isEditMode: Boolean) : LinkListEvent
    data object CloseEdit : LinkListEvent
    data object Finish : LinkListEvent
}
