package com.nexters.boolti.domain.request

import com.nexters.boolti.domain.model.PaymentType

sealed class TicketingRequest {
    open val userId: String = ""
    open val showId: String = ""
    open val salesTicketTypeId: String = ""
    open val reservationName: String = ""
    open val reservationPhoneNumber: String = ""

    data class Normal(
        val ticketCount: Int,
        val depositorName: String,
        val depositorPhoneNumber: String,
        val paymentAmount: Int,
        val paymentType: PaymentType,
        override val userId: String,
        override val showId: String,
        override val salesTicketTypeId: String,
        override val reservationName: String,
        override val reservationPhoneNumber: String,
    ) : TicketingRequest()

    data class Invite(
        val inviteCode: String,
        override val userId: String,
        override val showId: String,
        override val salesTicketTypeId: String,
        override val reservationName: String,
        override val reservationPhoneNumber: String,
    ) : TicketingRequest()
}
