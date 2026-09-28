package com.nexters.boolti.domain.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaymentApproveRequest(
    @SerialName("orderId")
    val orderId: String,
    @SerialName("amount")
    val amount: Int,
    @SerialName("paymentKey")
    val paymentKey: String,
    @SerialName("showId")
    val showId: String,
    @SerialName("salesTicketTypeId")
    val salesTicketTypeId: String,
    @SerialName("ticketCount")
    val ticketCount: Int,
    @SerialName("reservationName")
    val reservationName: String,
    @SerialName("reservationPhoneNumber")
    val reservationPhoneNumber: String,
    @SerialName("depositorName")
    val depositorName: String,
    @SerialName("depositorPhoneNumber")
    val depositorPhoneNumber: String,
)
