package com.nexters.boolti.domain.request

import com.nexters.boolti.domain.model.PaymentType
import kotlinx.serialization.Serializable

@Serializable
data class PaymentApproveRequest(
    val orderId: String,
    val amount: Int,
    val paymentKey: String,
    val showId: String,
    val salesTicketTypeId: String,
    val ticketCount: Int,
    val reservationName: String,
    val reservationPhoneNumber: String,
    val depositorName: String,
    val depositorPhoneNumber: String,
    val paymentAmount: Int,
    val means: PaymentType,
)
