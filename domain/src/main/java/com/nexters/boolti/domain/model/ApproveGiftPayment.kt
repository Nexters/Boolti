package com.nexters.boolti.domain.model

data class ApproveGiftPayment(
    val orderId: String,
    val reservationId: String,
    val giftId: String,
    val giftUuid: String,
)
