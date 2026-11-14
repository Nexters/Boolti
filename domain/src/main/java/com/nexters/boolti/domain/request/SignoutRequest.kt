package com.nexters.boolti.domain.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SignoutRequest(
    @SerialName("reason")
    val reason: String,
)
