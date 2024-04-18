package com.nexters.boolti.data.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApprovePaymentResponse(
    @SerialName("orderId") val orderId: String,
)
