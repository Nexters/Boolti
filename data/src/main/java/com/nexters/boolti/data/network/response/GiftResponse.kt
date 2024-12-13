package com.nexters.boolti.data.network.response

import com.nexters.boolti.data.util.toLocalDate
import com.nexters.boolti.domain.model.Gift
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GiftResponse(
    val id: String,
    @SerialName("userId") val senderUserId: String,
    val giftUuid: String,
    val orderId: String?,
    val reservationId: String,
    val giftImgId: String,
    val giftImgPath: String,
    val message: String,
    val senderName: String,
    val senderPhoneNumber: String,
    val recipientName: String,
    val recipientPhoneNumber: String,
    val salesEndTime: String,
    val isDone: Boolean,
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
        )
    }
}
