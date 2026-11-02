package com.nexters.boolti.data.network.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GiftReceiveRequest(
    @SerialName("giftUuid")
    val giftUuid: String,
)