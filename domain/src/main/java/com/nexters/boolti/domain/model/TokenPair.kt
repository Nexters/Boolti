package com.nexters.boolti.domain.model

data class TokenPair(
    val accessToken: String,
    val refreshToken: String,
) {
    val isLoggedIn: Boolean = accessToken.isNotBlank() && refreshToken.isNotBlank()
}
