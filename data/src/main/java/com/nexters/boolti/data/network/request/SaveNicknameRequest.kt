package com.nexters.boolti.data.network.request

import kotlinx.serialization.Serializable

@Serializable
data class SaveNicknameRequest(
    val nickname: String,
)
