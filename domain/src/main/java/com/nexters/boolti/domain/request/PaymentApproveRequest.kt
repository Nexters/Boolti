package com.nexters.boolti.domain.request

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
)
