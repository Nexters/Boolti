package com.nexters.boolti.data.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderIdDto(
    @SerialName("orderId") val orderId: String,
)
