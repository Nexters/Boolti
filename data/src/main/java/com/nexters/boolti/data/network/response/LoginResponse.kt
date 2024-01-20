package com.nexters.boolti.data.network.response

import kotlinx.serialization.SerialName

data class LoginResponse(
    @SerialName("signupRequired") val signupRequired: Boolean,
    @SerialName("accessToken") val accessToken: String?,
    @SerialName("refreshToken") val refreshToken: String?,
)
