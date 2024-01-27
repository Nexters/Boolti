package com.nexters.boolti.presentation.screen.ticket

import com.nexters.boolti.domain.model.TicketingTicket

data class TicketingState(
    val poster: String,
    val ticket: TicketingTicket,
    val isSameContactInfo: Boolean = false,
    val inviteCodeStatus: InviteCodeStatus = InviteCodeStatus.Default,
)

sealed interface InviteCodeStatus {
    data object Default : InviteCodeStatus
    data object Empty : InviteCodeStatus
    data object Invalid : InviteCodeStatus
    data object Duplicated : InviteCodeStatus
    data object Valid : InviteCodeStatus
}
