package com.nexters.boolti.presentation.util.bridge

import kotlinx.serialization.Serializable

interface BridgeCallbackHandler {
    fun fetchToken(): TokenDto
}

@Serializable
data class TokenDto(
    val token: String,
)
