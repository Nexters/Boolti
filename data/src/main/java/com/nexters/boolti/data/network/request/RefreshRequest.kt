package com.nexters.boolti.data.network.request

import kotlinx.serialization.Serializable

@Serializable
internal data class RefreshRequest(
    val refreshToken: String,
)
