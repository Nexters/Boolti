package com.nexters.boolti.domain.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TicketingInfoRequest(
    @SerialName("showId")
    val showId: String,
    @SerialName("salesTicketId")
    val salesTicketId: String,
    @SerialName("ticketCount")
    val ticketCount: Int,
)
