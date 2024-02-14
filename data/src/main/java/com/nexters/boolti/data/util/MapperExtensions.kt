package com.nexters.boolti.data.util

import com.nexters.boolti.domain.model.PaymentType
import com.nexters.boolti.domain.model.ReservationState
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

internal fun String.toLocalDate(): LocalDate = this.toLocalDateTime().toLocalDate()

internal fun String.toLocalDateTime(): LocalDateTime = LocalDateTime.parse(this.format(formatter))

internal fun String.toReservationState(): ReservationState {
    return when (this) {
        "WAITING_FOR_DEPOSIT" -> ReservationState.DEPOSITING
        "CANCELLED" -> ReservationState.CANCELED
        "RESERVATION_COMPLETED" -> ReservationState.RESERVED
        "WAITING_FOR_REFUND" -> ReservationState.REFUNDING
        "REFUND_COMPLETED" -> ReservationState.REFUNDED
        else -> ReservationState.UNDEFINED
    }
}

internal fun String?.toPaymentType(): PaymentType {
    return when (this) {
        "BANK_TRANSFER", "ACCOUNT_TRANSFER" -> PaymentType.ACCOUNT_TRANSFER
        "CARD" -> PaymentType.CARD
        else -> PaymentType.UNDEFINED
    }
}
