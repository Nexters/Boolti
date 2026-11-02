package com.nexters.boolti.data.network.request

import com.nexters.boolti.domain.request.TicketingRequest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ReservationInviteTicketRequest(
    @SerialName("userId")
    val userId: String,
    @SerialName("showId")
    val showId: String,
    @SerialName("salesTicketTypeId")
    val salesTicketTypeId: String,
    @SerialName("reservationName")
    val reservationName: String,
    @SerialName("reservationPhoneNumber")
    val reservationPhoneNumber: String,
    @SerialName("inviteCode")
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
