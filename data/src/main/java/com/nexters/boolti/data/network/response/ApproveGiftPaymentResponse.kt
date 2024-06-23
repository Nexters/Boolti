package com.nexters.boolti.data.network.response

data class ApproveGiftPaymentResponse(
    val orderId: String,
    val reservationId: String,
    val giftId: String,
)