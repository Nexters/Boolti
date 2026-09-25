package com.nexters.boolti.data.network.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SaveIntroduceRequest(
    @SerialName("introduction")
    val introduction: String,
)
