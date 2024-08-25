package com.nexters.boolti.presentation.screen.profileedit.profile

data class ProfileEditState(
    val loading: Boolean = false,
    val thumbnail: String = "",
    val nickname: String = "",
    val introduction: String = "",
    val links: List<Pair<String, String>> = emptyList(),
)
