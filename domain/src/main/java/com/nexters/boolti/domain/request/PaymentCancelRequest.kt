package com.nexters.boolti.domain.request

import kotlinx.serialization.Serializable

@Serializable
data class PaymentCancelRequest(
    val reservationId: String,
    val cancelReason: String,
)
