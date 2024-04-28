package com.nexters.boolti.data.network.request

import com.nexters.boolti.domain.request.TicketingRequest
import kotlinx.serialization.Serializable

@Serializable
internal data class ReservationSalesTicketRequest(
    val userId: String,
    val showId: String,
    val salesTicketTypeId: String,
    val ticketCount: Int,
    val reservationName: String,
    val reservationPhoneNumber: String,
)

internal fun TicketingRequest.Free.toData(): ReservationSalesTicketRequest = ReservationSalesTicketRequest(
    userId = userId,
    showId = showId,
    salesTicketTypeId = salesTicketTypeId,
    ticketCount = ticketCount,
    reservationName = reservationName,
    reservationPhoneNumber = reservationPhoneNumber,
)
