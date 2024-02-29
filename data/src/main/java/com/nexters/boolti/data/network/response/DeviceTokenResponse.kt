package com.nexters.boolti.data.network.response

import kotlinx.serialization.Serializable

@Serializable
data class DeviceTokenResponse(
    val tokenId: String
)