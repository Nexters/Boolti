package com.nexters.boolti.presentation.screen.ticketing

import com.nexters.boolti.domain.model.InviteCodeStatus
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
    val paymentType: PaymentType = PaymentType.ACCOUNT_TRANSFER,
    val reservationName: String = "",
    val reservationPhoneNumber: String = "",
    val depositorName: String = "",
    val depositorPhoneNumber: String = "",
    val inviteCode: String = "",
    val refundPolicy: List<String> = emptyList(),
) {
    val reservationButtonEnabled: Boolean
        get() = if (isInviteTicket) {
            reservationName.isNotBlank() &&
                    reservationPhoneNumber.isNotBlank() &&
                    inviteCodeStatus is InviteCodeStatus.Valid
        } else {
            reservationName.isNotBlank() &&
                    reservationPhoneNumber.isNotBlank() &&
                    (isSameContactInfo || depositorName.isNotBlank()) &&
                    (isSameContactInfo || depositorPhoneNumber.isNotBlank())
        }
}
