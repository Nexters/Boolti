package com.nexters.boolti.data.network.response

import com.nexters.boolti.domain.model.SalesTicket
import com.nexters.boolti.domain.model.TicketWithQuantity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class SalesTicketTypeDto(
    @SerialName("id") val id: String,
    @SerialName("showId") val showId: String,
    @SerialName("ticketType") val ticketType: String,
    @SerialName("ticketName") val ticketName: String,
    @SerialName("price") val price: Int,
    @SerialName("quantity") val quantity: Int,
) {
    fun toDomain(): TicketWithQuantity = TicketWithQuantity(
        ticket = when (ticketType.trim().uppercase()) {
            "INVITE" -> SalesTicket(id = id, showId = showId, ticketName = ticketName, price = price, isInviteTicket = true)
            else -> SalesTicket(id = id, showId = showId, ticketName = ticketName, price = price, isInviteTicket = false)
        },
        quantity = quantity,
    )
}
