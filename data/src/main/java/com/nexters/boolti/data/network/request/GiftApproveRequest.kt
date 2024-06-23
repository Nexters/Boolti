package com.nexters.boolti.data.network.request

data class GiftApproveRequest(
    val orderId: String,
    val amount: Int,
    val paymentKey: String,
    val showId: String,
    val salesTicketTypeId: String,
    val message: String,
    val senderName: String,
    val senderPhoneNumber: String,
    val recipientName: String,
    val recipientPhoneNumber: String,
)
