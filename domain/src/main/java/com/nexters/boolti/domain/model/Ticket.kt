package com.nexters.boolti.domain.model

sealed class Ticket(
    open val id: String,
    open val showId: String,
    open val ticketName: String,
    open val price: Int,
) {
    data class Sale(
        override val id: String,
        override val showId: String,
        override val ticketName: String,
        override val price: Int,
    ) : Ticket(id, showId, ticketName, price)

    data class Invite(
        override val id: String,
        override val showId: String,
        override val ticketName: String,
        override val price: Int,
    ) : Ticket(id, showId, ticketName, price)

    val isInviteTicket: Boolean
        get() = this is Invite
}

data class TicketWithQuantity(
    val ticket: Ticket,
    val quantity: Int,
)
