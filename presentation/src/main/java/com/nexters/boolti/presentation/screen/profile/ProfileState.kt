package com.nexters.boolti.presentation.screen.profile

import com.nexters.boolti.domain.model.User
import com.nexters.boolti.domain.model.YouTubeVideo

data class ProfileState(
    val loading: Boolean = false,
    val user: User = User.My(""),
    val isMine: Boolean = false,
    val youtubeVideos: List<YouTubeVideo> = emptyList(),
)
