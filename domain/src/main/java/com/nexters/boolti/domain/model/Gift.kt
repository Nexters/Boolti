package com.nexters.boolti.domain.model

data class Gift(
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
