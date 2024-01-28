package com.nexters.boolti.presentation.screen.ticket

import com.nexters.boolti.domain.model.TicketingTicket

data class TicketingState(
    val loading: Boolean = false,
    val poster: String = "",
    val isSameContactInfo: Boolean = false,
    val inviteCodeStatus: InviteCodeStatus = InviteCodeStatus.Default,
    val ticket: TicketingTicket? = null,
    private val leftAmount: Map<String, Int> = emptyMap(),
)

sealed interface InviteCodeStatus {
    data object Default : InviteCodeStatus
    data object Empty : InviteCodeStatus
    data object Invalid : InviteCodeStatus
    data object Duplicated : InviteCodeStatus
    data object Valid : InviteCodeStatus
}
