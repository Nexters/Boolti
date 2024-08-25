package com.nexters.boolti.presentation.screen.profileedit.link

data class LinkEditState(
    val linkName: String,
    val url: String,
    val isEditMode: Boolean = false,
) {
    constructor(
        linkName: String,
        url: String,
    ) : this(linkName, url, linkName.isNotBlank() || url.isNotBlank())
}
