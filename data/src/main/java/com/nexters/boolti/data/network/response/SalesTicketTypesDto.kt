package com.nexters.boolti.data.network.response

import com.nexters.boolti.domain.model.Ticket
import com.nexters.boolti.domain.model.TicketWithQuantity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SalesTicketTypeDto(
    @SerialName("id") val id: String,
    @SerialName("showId") val showId: String,
    @SerialName("ticketType") val ticketType: String,
    @SerialName("ticketName") val ticketName: String,
    @SerialName("price") val price: Int,
    @SerialName("quantity") val quantity: Int,
) {
    fun toDomain(): TicketWithQuantity = TicketWithQuantity(
        ticket = when (ticketType.trim().uppercase()) {
            "INVITE" -> Ticket.Invite(id = id, showId = showId, ticketName = ticketName, price = price)
            else -> Ticket.Sale(id = id, showId = showId, ticketName = ticketName, price = price)
        },
        quantity = quantity,
    )
}
