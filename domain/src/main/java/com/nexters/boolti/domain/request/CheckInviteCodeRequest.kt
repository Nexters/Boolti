package com.nexters.boolti.domain.request

import kotlinx.serialization.Serializable

@Serializable
data class CheckInviteCodeRequest(
    val showId: String,
    val salesTicketId: String,
    val inviteCode: String,
)
