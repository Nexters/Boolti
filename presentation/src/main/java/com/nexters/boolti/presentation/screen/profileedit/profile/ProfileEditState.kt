package com.nexters.boolti.presentation.screen.profileedit.profile

data class ProfileEditState(
    val loading: Boolean = false,
    val saving: Boolean = false,
    val thumbnail: String = "",
    val nickname: String = "",
    val id: String = "",
    val introduction: String = "",
    val snsCount: Int = 0,
    val upcomingShowCount: Int = 0,
    val pastShowCount: Int = 0,
    val videoCount: Int = 0,
    val linkCount: Int = 0,
    val showUpcomingShows: Boolean = false,
    val showPerformedShows: Boolean = false,
)
