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
    val csReservationId: String,
    val showImg: String,
    val showName: String,
    val showDate: String,
    val salesTicketName: String,
    val salesTicketType: String,
    val ticketCount: Int,
    val salesEndTime: String,
    val meansType: String?,
    val totalAmountPrice: Int = 0,
    val reservationStatus: String,
    val senderName: String,
    val senderPhoneNumber: String,
    val recipientName: String = "",
    val recipientPhoneNumber: String = "",
    val giftId: String,
    val giftUuid: String,
    val giftMessage: String,
    val giftInvitePath: String,
    val cardDetail: CardDetailResponse? = null,
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