package com.nexters.boolti.data.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class DeviceTokenResponse(
    @SerialName("tokenId")
    val tokenId: String
)
