package com.nexters.boolti.data.network.response

import com.nexters.boolti.domain.model.Gift
import kotlinx.serialization.Serializable

@Serializable
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
) {
    fun toDomain(): Gift {
        return Gift(
            id = id,
            orderId = orderId,
            reservationId = reservationId,
            giftImgId = giftImgId,
            message = message,
            senderName = senderName,
            senderPhoneNumber = senderPhoneNumber,
            recipientName = recipientName,
            recipientPhoneNumber = recipientPhoneNumber,
            isDone = isDone,
        )
    }
}
