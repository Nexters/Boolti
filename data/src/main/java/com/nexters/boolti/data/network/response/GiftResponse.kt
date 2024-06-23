package com.nexters.boolti.data.network.response

data class GiftResponse(
    val id: String,
    val orderId: String,
    val reservationId: String,
    val giftImgId: String,
    val message: String,
    val senderName: String,
    val senderPhoneNumber: String,
    val recipientName: String,
    val recipientPhoneNumber: String,
    val isDone: Boolean,
)
