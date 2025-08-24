package com.nexters.boolti.presentation.screen.link

import com.nexters.boolti.domain.model.Link

data class LinkListState(
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
