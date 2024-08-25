package com.nexters.boolti.presentation.screen.profileedit.profile

import com.nexters.boolti.domain.model.Link

data class ProfileEditState(
    val loading: Boolean = false,
    val thumbnail: String = "",
    val nickname: String = "",
    val introduction: String = "",
    val links: List<Link> = emptyList(),
)
