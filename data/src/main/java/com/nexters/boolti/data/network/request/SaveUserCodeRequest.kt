package com.nexters.boolti.data.network.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SaveUserCodeRequest(
    @SerialName("userCode") val userCode: String,
)
