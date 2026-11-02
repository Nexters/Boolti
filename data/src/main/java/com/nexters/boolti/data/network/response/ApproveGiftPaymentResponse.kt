package com.nexters.boolti.data.network.response

import com.nexters.boolti.domain.model.ApproveGiftPayment
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApproveGiftPaymentResponse(
    @SerialName("orderId")
    val orderId: String = "-1", // 무료 티켓일 경우 orderId가 null
    @SerialName("reservationId")
    val reservationId: String,
    @SerialName("giftId")
    val giftId: String,
    @SerialName("giftUuid")
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