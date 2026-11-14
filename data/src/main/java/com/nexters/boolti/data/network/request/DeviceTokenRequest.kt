package com.nexters.boolti.data.network.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class DeviceTokenRequest(
    @SerialName("deviceToken")
    val deviceToken: String,
    @SerialName("deviceType")
    val deviceType: String,
)
