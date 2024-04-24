package com.nexters.boolti.data.network.response

import com.nexters.boolti.data.util.toLocalDateTime
import com.nexters.boolti.data.util.toPaymentType
import com.nexters.boolti.data.util.toReservationState
import com.nexters.boolti.domain.model.ReservationDetail
import kotlinx.serialization.Serializable

@Serializable
internal data class ReservationDetailResponse(
    val reservationId: String,
    val showImg: String,
    val showName: String,
    val showDate: String,
    val salesTicketName: String,
    val salesTicketType: String,
    val ticketCount: Int,
    val bankName: String,
    val accountNumber: String,
    val accountHolder: String,
    val salesEndTime: String,
    val meansType: String?,
    val totalAmountPrice: Int = 0,
    val reservationStatus: String,
    val completedTimeStamp: String?,
    val reservationName: String,
    val reservationPhoneNumber: String,
    val depositorName: String = "",
    val depositorPhoneNumber: String = "",
    val csReservationId: String,
    val cardDetail: CardDetailResponse? = null,
) {
    fun toDomain(): ReservationDetail {
        return ReservationDetail(
            id = reservationId,
            showImage = showImg,
            showName = showName,
            showDate = showDate.toLocalDateTime(),
            ticketName = salesTicketName,
            isInviteTicket = salesTicketType == "INVITE",
            ticketCount = ticketCount,
            bankName = bankName,
            accountNumber = accountNumber,
            accountHolder = accountHolder,
            salesEndDateTime = salesEndTime.toLocalDateTime(),
            paymentType = meansType.toPaymentType(),
            totalAmountPrice = totalAmountPrice,
            reservationState = reservationStatus.toReservationState(),
            completedDateTime = completedTimeStamp?.toLocalDateTime(),
            ticketHolderName = reservationName,
            ticketHolderPhoneNumber = reservationPhoneNumber,
            depositorName = depositorName,
            depositorPhoneNumber = depositorPhoneNumber,
            csReservationId = csReservationId,
            cardDetail = cardDetail?.toDomain(),
        )
    }

    @Serializable
    internal data class CardDetailResponse(
        val installmentPlanMonths: Int,
        val issuerCode: String,
    ) {
        fun toDomain(): ReservationDetail.CardDetail = ReservationDetail.CardDetail(
            installmentPlanMonths = installmentPlanMonths,
            issuerCode = issuerCode,
        )
    }
}
