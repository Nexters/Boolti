package com.nexters.boolti.data.network.request

import com.nexters.boolti.domain.model.PaymentType.ACCOUNT_TRANSFER
import com.nexters.boolti.domain.model.PaymentType.CARD
import com.nexters.boolti.domain.model.PaymentType.FREE
import com.nexters.boolti.domain.model.PaymentType.SIMPLE_PAYMENT
import com.nexters.boolti.domain.model.PaymentType.UNDEFINED
import com.nexters.boolti.domain.request.TicketingRequest
import kotlinx.serialization.Serializable

@Serializable
internal data class ReservationSalesTicketRequest(
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

internal fun TicketingRequest.Normal.toData(): ReservationSalesTicketRequest = ReservationSalesTicketRequest(
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
        ACCOUNT_TRANSFER -> "ACCOUNT_TRANSFER"
        CARD -> "CARD"
        SIMPLE_PAYMENT -> "SIMPLE_PAYMENT"
        FREE -> "FREE"
        UNDEFINED -> ""
    },
)
