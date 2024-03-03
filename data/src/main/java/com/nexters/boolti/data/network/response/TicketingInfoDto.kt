package com.nexters.boolti.data.network.response

import com.nexters.boolti.data.util.toLocalDateTime
import com.nexters.boolti.domain.model.PaymentType
import com.nexters.boolti.domain.model.TicketingInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class TicketingInfoDto(
    @SerialName("meansType")
    val meansType: String,
    @SerialName("salesTicketType")
    val salesTicketType: String,
    @SerialName("saleTicketName")
    val salesTicketName: String,
    @SerialName("showDate")
    val showDate: String,
    @SerialName("showImg")
    val showImg: String,
    @SerialName("showName")
    val showName: String,
    @SerialName("ticketCount")
    val ticketCount: Int,
    @SerialName("totalAmountPrice")
    val totalAmountPrice: Int,
) {
    fun toDomain(): TicketingInfo = TicketingInfo(
        saleTicketName = salesTicketName,
        showDate = showDate.toLocalDateTime(),
        showImg = showImg,
        showName = showName,
        ticketCount = ticketCount,
        totalPrice = totalAmountPrice,
        isInviteTicket = salesTicketType.trim().uppercase() == "INVITE",
        paymentType = when (meansType.trim().uppercase()) {
            "CARD" -> PaymentType.CARD
            else -> PaymentType.ACCOUNT_TRANSFER
        },
    )
}
