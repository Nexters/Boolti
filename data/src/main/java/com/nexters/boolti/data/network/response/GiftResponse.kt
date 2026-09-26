package com.nexters.boolti.data.network.response

import com.nexters.boolti.data.util.toLocalDate
import com.nexters.boolti.data.util.toLocalDateTime
import com.nexters.boolti.domain.model.Gift
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GiftResponse(
    @SerialName("id")
    val id: String,
    @SerialName("userId") val senderUserId: String,
    @SerialName("giftUuid")
    val giftUuid: String,
    @SerialName("orderId")
    val orderId: String?,
    @SerialName("reservationId")
    val reservationId: String,
    @SerialName("giftImgId")
    val giftImgId: String,
    @SerialName("giftImgPath")
    val giftImgPath: String,
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
    @SerialName("salesEndTime")
    val salesEndTime: String,
    @SerialName("isDone")
    val isDone: Boolean,
    @SerialName("showId")
    val showId: String,
    @SerialName("showName")
    val showName: String,
    @SerialName("showImg")
    val showImg: String,
    @SerialName("showDate")
    val showDate: String,
    @SerialName("salesTicketName")
    val salesTicketName: String,
    @SerialName("ticketCount")
    val ticketCount: Int,
) {
    fun toDomain(): Gift {
        return Gift(
            id = id,
            senderUserId = senderUserId,
            uuid = giftUuid,
            orderId = orderId,
            reservationId = reservationId,
            giftImgId = giftImgId,
            imagePath = giftImgPath,
            message = message,
            senderName = senderName,
            senderPhoneNumber = senderPhoneNumber,
            recipientName = recipientName,
            recipientPhoneNumber = recipientPhoneNumber,
            salesEndTime = salesEndTime.toLocalDate(),
            isDone = isDone,
            showId = showId,
            showName = showName,
            showImage = showImg,
            showDate = showDate.toLocalDateTime(),
            salesTicketName = salesTicketName,
            ticketCount = ticketCount,
        )
    }
}
