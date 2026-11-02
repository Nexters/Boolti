package com.nexters.boolti.domain.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderIdRequest(
    @SerialName("showId")
    val showId: String,
    @SerialName("salesTicketTypeId")
    val salesTicketTypeId: String,
    @SerialName("ticketCount")
    val ticketCount: Int,
)
