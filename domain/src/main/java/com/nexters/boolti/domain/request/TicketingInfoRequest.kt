package com.nexters.boolti.domain.request

import kotlinx.serialization.Serializable

@Serializable
data class TicketingInfoRequest(
    val showId: String,
    val salesTicketId: String,
    val ticketCount: Int,
)
