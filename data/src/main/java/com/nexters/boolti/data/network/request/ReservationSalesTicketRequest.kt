package com.nexters.boolti.data.network.request

import com.nexters.boolti.domain.request.TicketingRequest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ReservationSalesTicketRequest(
    @SerialName("userId")
    val userId: String,
    @SerialName("showId")
    val showId: String,
    @SerialName("salesTicketTypeId")
    val salesTicketTypeId: String,
    @SerialName("ticketCount")
    val ticketCount: Int,
    @SerialName("reservationName")
    val reservationName: String,
    @SerialName("reservationPhoneNumber")
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
