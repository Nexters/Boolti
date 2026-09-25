package com.nexters.boolti.data.network.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class RefreshRequest(
    @SerialName("refreshToken")
    val refreshToken: String,
)
