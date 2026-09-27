package com.nexters.boolti.data.network.response

import com.nexters.boolti.data.util.toLocalDateTime
import com.nexters.boolti.data.util.toPaymentType
import com.nexters.boolti.data.util.toReservationState
import com.nexters.boolti.domain.model.ReservationDetail
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ReservationDetailResponse(
    @SerialName("reservationId")
    val reservationId: String,
    @SerialName("showImg")
    val showImg: String,
    @SerialName("showName")
    val showName: String,
    @SerialName("showDate")
    val showDate: String,
    @SerialName("salesTicketName")
    val salesTicketName: String,
    @SerialName("salesTicketType")
    val salesTicketType: String,
    @SerialName("ticketCount")
    val ticketCount: Int,
    @SerialName("bankName")
    val bankName: String? = "",
    @SerialName("accountNumber")
    val accountNumber: String? = "",
    @SerialName("accountHolder")
    val accountHolder: String? = "",
    @SerialName("salesEndTime")
    val salesEndTime: String,
    @SerialName("meansType")
    val meansType: String?,
    @SerialName("totalAmountPrice")
    val totalAmountPrice: Int = 0,
    @SerialName("reservationStatus")
    val reservationStatus: String,
    @SerialName("completedTimeStamp")
    val completedTimeStamp: String?,
    @SerialName("reservationName")
    val reservationName: String,
    @SerialName("reservationPhoneNumber")
    val reservationPhoneNumber: String,
    @SerialName("depositorName")
    val depositorName: String = "",
    @SerialName("depositorPhoneNumber")
    val depositorPhoneNumber: String = "",
    @SerialName("csReservationId")
    val csReservationId: String,
    @SerialName("cardDetail")
    val cardDetail: CardDetailResponse? = null,
    @SerialName("easyPayDetail")
    val easyPayDetail: EasyPayDetailResponse? = null,
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
            bankName = bankName ?: "",
            accountNumber = accountNumber ?: "",
            accountHolder = accountHolder ?: "",
            salesEndDateTime = salesEndTime.toLocalDateTime(),
            paymentType = meansType.toPaymentType(),
            totalAmountPrice = totalAmountPrice,
            reservationState = reservationStatus.toReservationState(),
            completedDateTime = completedTimeStamp?.toLocalDateTime(),
            visitorName = reservationName,
            visitorPhoneNumber = reservationPhoneNumber,
            depositorName = depositorName,
            depositorPhoneNumber = depositorPhoneNumber,
            csReservationId = csReservationId,
            cardDetail = cardDetail?.toDomain(),
            provider = easyPayDetail?.provider ?: ""
        )
    }

    @Serializable
    internal data class CardDetailResponse(
        @SerialName("installmentPlanMonths")
        val installmentPlanMonths: Int,
        @SerialName("issuerCode")
        val issuerCode: String,
    ) {
        fun toDomain(): ReservationDetail.CardDetail = ReservationDetail.CardDetail(
            installmentPlanMonths = installmentPlanMonths,
            issuerCode = issuerCode,
        )
    }

    @Serializable
    internal data class EasyPayDetailResponse(
        @SerialName("provider")
        val provider: String?,
    )
}
