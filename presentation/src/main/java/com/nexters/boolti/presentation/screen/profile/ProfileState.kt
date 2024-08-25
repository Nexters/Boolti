package com.nexters.boolti.presentation.screen.profile

import com.nexters.boolti.domain.model.User

data class ProfileState(
    val loading: Boolean = false,
    val user: User = User(""),
    val isMine: Boolean = false,
)
