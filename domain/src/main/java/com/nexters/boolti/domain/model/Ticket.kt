package com.nexters.boolti.domain.model

data class Ticket(
    val id: String,
    val showId: String,
    val ticketName: String,
    val price: Int,
    val isInviteTicket: Boolean,
)

data class TicketWithQuantity(
    val ticket: Ticket,
    val quantity: Int,
)
