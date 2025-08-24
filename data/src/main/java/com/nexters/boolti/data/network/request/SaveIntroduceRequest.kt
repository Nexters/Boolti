package com.nexters.boolti.data.network.request

import kotlinx.serialization.Serializable

@Serializable
data class SaveIntroduceRequest(
    val introduction: String,
)
