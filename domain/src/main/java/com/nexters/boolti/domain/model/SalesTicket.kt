package com.nexters.boolti.domain.model

/**
 * 예매 티켓
 */
data class SalesTicket(
    val id: String,
    val showId: String,
    val ticketName: String,
    val price: Int,
    val isInviteTicket: Boolean,
)

data class TicketWithQuantity(
    val ticket: SalesTicket,
    val quantity: Int,
)
