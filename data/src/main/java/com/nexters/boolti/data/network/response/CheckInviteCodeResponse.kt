package com.nexters.boolti.data.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CheckInviteCodeResponse(
    @SerialName("id") val id: String,
    @SerialName("code") val inviteCode: String,
    @SerialName("isUsed") val isUsed: Boolean,
)
