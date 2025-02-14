package com.nexters.boolti.data.util

import com.nexters.boolti.domain.model.PaymentType
import com.nexters.boolti.domain.model.ReservationState
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

// 2024-12-29T00:00:00
internal fun String.toLocalDate(): LocalDate = this.toLocalDateTime().toLocalDate()

internal fun Long.toLocalDate(): LocalDate = LocalDate.ofEpochDay(this)

internal fun String.toLocalDateTime(): LocalDateTime = LocalDateTime.parse(this.format(formatter))

internal fun String.toReservationState(): ReservationState {
    return when (this) {
        "WAITING_FOR_DEPOSIT" -> ReservationState.DEPOSITING
        "CANCELLED" -> ReservationState.CANCELED
        "RESERVATION_COMPLETED" -> ReservationState.RESERVED
        "WAITING_FOR_REFUND" -> ReservationState.REFUNDING
        "REFUND_COMPLETED" -> ReservationState.REFUNDED
        "WAITING_FOR_GIFT_RECEIPT" -> ReservationState.REGISTERING_GIFT
        // TODO: 선물 등록 완료 상태 추가
        else -> ReservationState.UNDEFINED
    }
}

internal fun String?.toPaymentType(): PaymentType {
    return when (this) {
        "BANK_TRANSFER", "ACCOUNT_TRANSFER" -> PaymentType.ACCOUNT_TRANSFER
        "CARD" -> PaymentType.CARD
        "SIMPLE_PAYMENT" -> PaymentType.SIMPLE_PAYMENT
        "FREE" -> PaymentType.FREE
        else -> PaymentType.UNDEFINED
    }
}

internal fun File.toImageMultipartBody(): MultipartBody.Part = MultipartBody.Part.createFormData(
    name = "image",
    filename = name,
    body = asRequestBody("image/*".toMediaType())
)
