package com.nexters.boolti.data.network.response

import com.nexters.boolti.domain.model.ApprovePaymentResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApprovePaymentResponse(
    @SerialName("orderId") val orderId: String,
    @SerialName("reservationId") val reservationId: String,
) {
    fun toDomain(): ApprovePaymentResponse = ApprovePaymentResponse(orderId, reservationId)
}
