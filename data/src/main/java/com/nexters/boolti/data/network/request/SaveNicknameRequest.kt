package com.nexters.boolti.data.network.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SaveNicknameRequest(
    @SerialName("nickname")
    val nickname: String,
)
