package com.nexters.boolti.domain.model

data class LoginUserState(
    val signUpRequired: Boolean = false,
    val signOutCancelled: Boolean = false,
)
