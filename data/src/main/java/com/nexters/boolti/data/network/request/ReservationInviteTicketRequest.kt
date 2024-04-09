package com.nexters.boolti.data.network.request

import com.nexters.boolti.domain.request.TicketingRequest
import kotlinx.serialization.Serializable

@Serializable
internal data class ReservationInviteTicketRequest(
    val userId: String,
    val showId: String,
    val salesTicketTypeId: String,
    val reservationName: String,
    val reservationPhoneNumber: String,
    val inviteCode: String,
)

internal fun TicketingRequest.Invite.toData(): ReservationInviteTicketRequest {
    return ReservationInviteTicketRequest(
        userId = userId,
        showId = showId,
        salesTicketTypeId = salesTicketTypeId,
        reservationName = reservationName,
        reservationPhoneNumber = reservationPhoneNumber,
        inviteCode = inviteCode,
    )
}
