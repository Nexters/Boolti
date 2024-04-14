package com.nexters.boolti.presentation.screen.ticketing

import com.nexters.boolti.domain.model.InviteCodeStatus
import com.nexters.boolti.domain.model.PaymentType
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
    val paymentType: PaymentType? = null,
    val reservationName: String = "",
    val reservationContact: String = "",
    val depositorName: String = "",
    val depositorContact: String = "",
    val inviteCode: String = "",
    val refundPolicy: List<String> = emptyList(),
    val orderAgreement: List<Boolean> = listOf(false, false, false),
) {
    val orderAgreementInfos = listOf(
        R.string.order_agreement_privacy_collection,
        R.string.order_agreement_privacy_offer,
        R.string.order_agreement_payment_agency,
    )

    val orderAgreed: Boolean
        get() = orderAgreement.none { !it }

    val reservationButtonEnabled: Boolean
        get() = when {
            !orderAgreed ||
                    reservationName.isBlank() ||
                    reservationContact.isBlank() -> false

            isInviteTicket -> inviteCodeStatus is InviteCodeStatus.Valid

            totalPrice == 0 -> true

            else -> paymentType != null &&
                    (isSameContactInfo || depositorName.isNotBlank()) &&
                    (isSameContactInfo || depositorContact.isNotBlank())
        }

    fun toggleAgreement(index: Int): TicketingState {
        val updated = orderAgreement.toMutableList().apply {
            set(index, !orderAgreement[index])
        }
        return copy(orderAgreement = updated)
    }

    fun toggleAgreement(): TicketingState {
        return if (orderAgreed) {
            copy(orderAgreement = orderAgreement.map { false })
        } else {
            copy(orderAgreement = orderAgreement.map { true })
        }
    }
}
