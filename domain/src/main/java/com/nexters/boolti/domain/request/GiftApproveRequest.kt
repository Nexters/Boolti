package com.nexters.boolti.domain.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GiftApproveRequest(
    val orderId: String,
    val amount: Int,
    val paymentKey: String,
    val showId: String,
    val salesTicketTypeId: String,
    val ticketCount: Int,
    @SerialName("giftImgId") val giftImageId: String,
    val message: String,
    val senderName: String,
    val senderPhoneNumber: String,
    val recipientName: String,
    val recipientPhoneNumber: String,
)
