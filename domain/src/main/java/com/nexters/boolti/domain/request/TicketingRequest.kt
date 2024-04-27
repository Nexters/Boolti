package com.nexters.boolti.domain.request

sealed class TicketingRequest {
    open val userId: String = ""
    open val showId: String = ""
    open val salesTicketTypeId: String = ""
    open val reservationName: String = ""
    open val reservationPhoneNumber: String = ""

    data class Free(
        val ticketCount: Int,
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
