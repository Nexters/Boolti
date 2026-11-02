package com.nexters.boolti.domain.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FreeGiftRequest(
    @SerialName("amount")
    val amount: Int,
    @SerialName("showId")
    val showId: String,
    @SerialName("salesTicketTypeId")
    val salesTicketTypeId: String,
    @SerialName("ticketCount")
    val ticketCount: Int,
    @SerialName("giftImgId") val giftImageId: String,
    @SerialName("message")
    val message: String,
    @SerialName("senderName")
    val senderName: String,
    @SerialName("senderPhoneNumber")
    val senderPhoneNumber: String,
    @SerialName("recipientName")
    val recipientName: String,
    @SerialName("recipientPhoneNumber")
    val recipientPhoneNumber: String,
)