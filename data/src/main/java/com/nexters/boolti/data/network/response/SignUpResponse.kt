package com.nexters.boolti.data.network.response

import kotlinx.serialization.SerialName

data class SignUpResponse(
    @SerialName("accessToken") val accessToken: String,
    @SerialName("refreshToken") val refreshToken: String,
)
