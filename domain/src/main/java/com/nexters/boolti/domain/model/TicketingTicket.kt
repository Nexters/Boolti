package com.nexters.boolti.domain.model

data class TicketingTicket(
    val id: String,
    val isInviteTicket: Boolean,
    val title: String,
    val price: Int,
)
