package com.nexters.boolti.data.network.request

import com.nexters.boolti.domain.model.PaymentType
import com.nexters.boolti.domain.request.TicketingRequest
import kotlinx.serialization.Serializable

@Serializable
data class ReservationSalesTicketRequest(
    val userId: String,
    val showId: String,
    val salesTicketTypeId: String,
    val ticketCount: Int,
    val reservationName: String,
    val reservationPhoneNumber: String,
    val depositorName: String,
    val depositorPhoneNumber: String,
    val paymentAmount: Int,
    val means: String,
)

fun TicketingRequest.Normal.toData(): ReservationSalesTicketRequest = ReservationSalesTicketRequest(
    userId = userId,
    showId = showId,
    salesTicketTypeId = salesTicketTypeId,
    ticketCount = ticketCount,
    reservationName = reservationName,
    reservationPhoneNumber = reservationPhoneNumber,
    depositorName = depositorName,
    depositorPhoneNumber = depositorPhoneNumber,
    paymentAmount = paymentAmount,
    means = when (paymentType) {
        PaymentType.CARD -> "CARD"
        PaymentType.ACCOUNT_TRANSFER -> "ACCOUNT_TRANSFER"
        PaymentType.UNDEFINED -> ""
    },
)
