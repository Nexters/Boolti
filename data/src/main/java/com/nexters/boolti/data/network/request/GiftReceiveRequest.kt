package com.nexters.boolti.data.network.request

import kotlinx.serialization.Serializable

@Serializable
data class GiftReceiveRequest(
    val giftUuid: String,
)