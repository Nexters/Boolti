package com.nexters.boolti.data.network.response

import com.nexters.boolti.domain.model.ApproveGiftPayment
import kotlinx.serialization.Serializable

@Serializable
data class ApproveGiftPaymentResponse(
    val orderId: String,
    val reservationId: String,
    val giftId: String,
    val giftUuid: String,
) {
    fun toDomain(): ApproveGiftPayment {
        return ApproveGiftPayment(
            orderId = orderId,
            reservationId = reservationId,
            giftId = giftId,
            giftUuid = giftUuid,
        )
    }
}