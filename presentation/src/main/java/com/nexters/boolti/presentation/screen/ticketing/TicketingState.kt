package com.nexters.boolti.presentation.screen.ticketing

import com.nexters.boolti.domain.model.PaymentType
import java.time.LocalDateTime

data class TicketingState(
    val loading: Boolean = false,
    val poster: String = "",
    val showDate: LocalDateTime = LocalDateTime.now(),
    val showName: String = "",
    val ticketName: String = "",
    val ticketCount: Int = 1,
    val totalPrice: Int = 0,
    val isSameContactInfo: Boolean = false,
    val isInviteTicket: Boolean = false,
    val inviteCodeStatus: InviteCodeStatus = InviteCodeStatus.Default,
    val paymentType: PaymentType = PaymentType.AccountTransfer,
)

sealed interface InviteCodeStatus {
    data object Default : InviteCodeStatus
    data object Empty : InviteCodeStatus
    data object Invalid : InviteCodeStatus
    data object Duplicated : InviteCodeStatus
    data object Valid : InviteCodeStatus
}
