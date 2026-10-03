package com.nexters.boolti.presentation.screen.link

import com.nexters.boolti.domain.model.Link
import com.nexters.boolti.presentation.screen.profileedit.link.LinkEditResult

data class LinkListUiState(
    val isMine: Boolean = false,
    val editing: Boolean = false,
    val originalLinks: List<Link> = emptyList(),
    val links: List<Link> = emptyList(),
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
    data class EditResultReceived(val result: LinkEditResult) : LinkListAction
}

sealed interface LinkListEvent {
    data object Added : LinkListEvent
    data object Edited : LinkListEvent
    data object Removed : LinkListEvent
    data class NavigateToAddLink(val closeListOnBack: Boolean) : LinkListEvent
    data class NavigateToEditLink(val link: Link) : LinkListEvent
    data object Finish : LinkListEvent
}
