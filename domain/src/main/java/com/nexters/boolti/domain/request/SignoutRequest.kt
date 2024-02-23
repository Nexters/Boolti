package com.nexters.boolti.domain.request

import kotlinx.serialization.Serializable

@Serializable
data class SignoutRequest(
    val reason: String,
)
