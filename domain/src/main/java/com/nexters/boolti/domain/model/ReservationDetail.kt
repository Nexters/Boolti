package com.nexters.boolti.domain.model

import java.time.LocalDateTime

data class ReservationDetail(
    val id: String,
    val showImage: String,
    val showName: String,
    val ticketName: String,
    val ticketCount: Int,
    val bankName: String,
    val accountNumber: String,
    val accountHolder: String,
    val salesEndDateTime: LocalDateTime,
    val paymentType: PaymentType,
    val totalAmountPrice: Int,
    val reservationState: ReservationState,
    val completedDateTime: LocalDateTime?,
    val ticketHolderName: String,
    val ticketHolderPhoneNumber: String,
    val depositorName: String,
    val depositorPhoneNumber: String,
)

enum class PaymentType {
    ACCOUNT_TRANSFER,
    CARD,
    UNDEFINED,
}
