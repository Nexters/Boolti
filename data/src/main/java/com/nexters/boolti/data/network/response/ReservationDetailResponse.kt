package com.nexters.boolti.data.network.response

import com.nexters.boolti.data.util.toLocalDateTime
import com.nexters.boolti.data.util.toReservationState
import com.nexters.boolti.domain.model.PaymentType
import com.nexters.boolti.domain.model.ReservationDetail
import kotlinx.serialization.Serializable

@Serializable
data class ReservationDetailResponse(
    val reservationId: String,
    val showImg: String,
    val showName: String,
    val salesTicketName: String,
    val ticketCount: Int,
    val bankName: String,
    val accountNumber: String,
    val accountHolder: String,
    val salesEndTime: String,
    val meansType: String,
    val totalAmountPrice: Int,
    val reservationStatus: String,
    val completedTimeStamp: String,
    val reservationName: String,
    val reservationPhoneNumber: String,
    val depositorName: String,
    val depositorPhoneNumber: String,
) {
    fun toDomain(): ReservationDetail {
        val paymentType = when (meansType) {
            "BANK_TRANSFER" -> PaymentType.ACCOUNT_TRANSFER
            "CARD" -> PaymentType.CARD
            else -> PaymentType.UNDEFINED
        }

        return ReservationDetail(
            id = reservationId,
            showImage = showImg,
            showName = showName,
            salesTicketName = salesTicketName,
            ticketCount = ticketCount,
            bankName = bankName,
            accountNumber = accountNumber,
            accountHolder = accountHolder,
            salesEndDateTime = salesEndTime.toLocalDateTime(),
            paymentType = paymentType,
            totalAmountPrice = totalAmountPrice,
            reservationState = reservationStatus.toReservationState(),
            completedDateTime = completedTimeStamp.toLocalDateTime(),
            ticketHolderName = reservationName,
            ticketHolderPhoneNumber = reservationPhoneNumber,
            depositorName = depositorName,
            depositorPhoneNumber = depositorPhoneNumber,
        )
    }
}
