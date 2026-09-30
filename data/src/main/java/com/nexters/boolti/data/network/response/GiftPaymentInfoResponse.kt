package com.nexters.boolti.data.network.response

import com.nexters.boolti.data.network.response.ReservationDetailResponse.CardDetailResponse
import com.nexters.boolti.data.network.response.ReservationDetailResponse.EasyPayDetailResponse
import com.nexters.boolti.data.util.toLocalDateTime
import com.nexters.boolti.data.util.toPaymentType
import com.nexters.boolti.data.util.toReservationState
import com.nexters.boolti.domain.model.ReservationDetail
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class GiftPaymentInfoResponse(
    @SerialName("csReservationId")
    val csReservationId: String,
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
    @SerialName("salesEndTime")
    val salesEndTime: String,
    @SerialName("meansType")
    val meansType: String?,
    @SerialName("totalAmountPrice")
    val totalAmountPrice: Int = 0,
    @SerialName("reservationStatus")
    val reservationStatus: String,
    @SerialName("senderName")
    val senderName: String,
    @SerialName("senderPhoneNumber")
    val senderPhoneNumber: String,
    @SerialName("recipientName")
    val recipientName: String = "",
    @SerialName("recipientPhoneNumber")
    val recipientPhoneNumber: String = "",
    @SerialName("giftId")
    val giftId: String,
    @SerialName("giftUuid")
    val giftUuid: String,
    @SerialName("giftMessage")
    val giftMessage: String,
    @SerialName("giftInvitePath")
    val giftInvitePath: String,
    @SerialName("cardDetail")
    val cardDetail: CardDetailResponse? = null,
    @SerialName("easyPayDetail")
    val easyPayDetail: EasyPayDetailResponse? = null,
) {
    fun toDomain(): ReservationDetail {
        return ReservationDetail(
            id = giftId,
            giftUuid = giftUuid,
            giftInviteImage = giftInvitePath,
            showImage = showImg,
            showName = showName,
            showDate = showDate.toLocalDateTime(),
            ticketName = salesTicketName,
            isInviteTicket = false,
            ticketCount = ticketCount,
            bankName = "",
            accountNumber = "",
            accountHolder = "",
            salesEndDateTime = salesEndTime.toLocalDateTime(),
            paymentType = meansType.toPaymentType(),
            totalAmountPrice = totalAmountPrice,
            reservationState = reservationStatus.toReservationState(),
            completedDateTime = null,
            visitorName = recipientName,
            visitorPhoneNumber = recipientPhoneNumber.toDashedPhoneNumber(),
            depositorName = senderName,
            depositorPhoneNumber = senderPhoneNumber.toDashedPhoneNumber(),
            csReservationId = csReservationId,
            cardDetail = cardDetail?.toDomain(),
            provider = easyPayDetail?.provider ?: ""
        )
    }

    private fun String.toDashedPhoneNumber(): String {
        if (!isPurePhoneNumber) return this

        return slice(0..2) + "-" + slice(3..6) + "-" + slice(7..10)
    }

    private val String.isPurePhoneNumber
        get() = "^\\d{11}$".toRegex().matches(this)
}