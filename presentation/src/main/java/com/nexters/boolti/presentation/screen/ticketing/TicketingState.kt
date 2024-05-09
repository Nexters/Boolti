package com.nexters.boolti.presentation.screen.ticketing

import com.nexters.boolti.domain.model.InviteCodeStatus
import com.nexters.boolti.presentation.R
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
    val reservationName: String = "",
    val reservationContact: String = "",
    val depositorName: String = "",
    val depositorContact: String = "",
    val inviteCode: String = "",
    val refundPolicy: List<String> = emptyList(),
    val orderAgreement: List<Pair<Int, Boolean>> = listOf(
        Pair(R.string.order_agreement_privacy_collection, false),
        Pair(R.string.order_agreement_privacy_offer, false),
    ),
) {
    val orderAgreed: Boolean
        get() = orderAgreement.none { !it.second }

    val reservationButtonEnabled: Boolean
        get() = when {
            !orderAgreed ||
                    reservationName.isBlank() ||
                    reservationContact.isBlank() -> false

            isInviteTicket -> inviteCodeStatus is InviteCodeStatus.Valid

            totalPrice == 0 -> true

            else -> (isSameContactInfo || depositorName.isNotBlank()) &&
                    (isSameContactInfo || depositorContact.isNotBlank())
        }

    fun toggleAgreement(): TicketingState = copy(orderAgreement = orderAgreement.map { it.copy(second = !orderAgreed) })
}
