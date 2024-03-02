package com.nexters.boolti.data.network.request

import kotlinx.serialization.Serializable

@Serializable
data class DeviceTokenRequest(
    val deviceToken: String,
    val deviceType: String,
)
