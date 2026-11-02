package com.nexters.boolti.domain.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaymentCancelRequest(
    @SerialName("reservationId")
    val reservationId: String,
    @SerialName("cancelReason")
    val cancelReason: String,
)
