package com.nexters.boolti.domain.request

import kotlinx.serialization.Serializable

@Serializable
data class OrderIdRequest(
    val showId: String,
    val salesTicketTypeId: String,
    val ticketCount: Int,
)
