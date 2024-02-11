package com.nexters.boolti.data.network.response

import com.nexters.boolti.data.util.toLocalDateTime
import com.nexters.boolti.domain.model.PaymentType
import com.nexters.boolti.domain.model.TicketingInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TicketingInfoDto(
    @SerialName("meansType")
    val meansType: String,
    @SerialName("saleTicketName")
    val saleTicketName: String,
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
        saleTicketName = saleTicketName,
        showDate = showDate.toLocalDateTime(),
        showImg = showImg,
        showName = showName,
        ticketCount = ticketCount,
        totalPrice = totalAmountPrice,
        paymentType = when (meansType.trim().uppercase()) {
            "CARD" -> PaymentType.Card
            else -> PaymentType.AccountTransfer
        },
    )
}
