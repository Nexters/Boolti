package com.nexters.boolti.data.network.response

import com.nexters.boolti.data.network.response.ReservationDetailResponse.CardDetailResponse
import com.nexters.boolti.data.network.response.ReservationDetailResponse.EasyPayDetailResponse
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
    val giftId: String,
    val giftUuid: String,
    val giftMessage: String,
    val recipientPhoneNumber: String = "",
    val cardDetail: CardDetailResponse? = null,
    val easyPayDetail: EasyPayDetailResponse? = null,
)