package com.nexters.boolti.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

data class Gift(
    val id: String,
    val senderUserId: String,
    val uuid: String,
    val orderId: String?,
    val reservationId: String,
    val giftImgId: String,
    val imagePath: String,
    val message: String,
    val senderName: String,
    val senderPhoneNumber: String,
    val recipientName: String,
    val recipientPhoneNumber: String,
    val salesEndTime: LocalDate,
    val isDone: Boolean,
    val showId: String,
    val showName: String,
    val showImage: String,
    val showDate: LocalDateTime,
    val salesTicketName: String,
    val ticketCount: Int,
)
