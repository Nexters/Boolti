package com.nexters.boolti.data.network.response

import com.nexters.boolti.data.util.toLocalDateTime
import com.nexters.boolti.data.util.toReservationState
import com.nexters.boolti.domain.model.Reservation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ReservationResponse(
    @SerialName("reservationId")
    val reservationId: String,
    @SerialName("reservationStatus")
    val reservationStatus: String,
    @SerialName("reservationDate")
    val reservationDate: String,
    @SerialName("giftId")
    val giftId: String?,
    @SerialName("showName")
    val showName: String,
    @SerialName("showImg")
    val showImg: String,
    @SerialName("salesTicketName")
    val salesTicketName: String,
    @SerialName("ticketCount")
    val ticketCount: Int,
    @SerialName("ticketPrice")
    val ticketPrice: Int = 0,
    @SerialName("recipientName")
    val recipientName: String?,
) {
    fun toDomain(): Reservation {
        return Reservation(
            id = reservationId,
            giftId = giftId,
            reservationState = reservationStatus.toReservationState(),
            reservationDateTime = reservationDate.toLocalDateTime(),
            showName = showName,
            showImage = showImg,
            salesTicketName = salesTicketName,
            ticketCount = ticketCount,
            ticketPrice = ticketPrice,
            receiver = recipientName,
        )
    }
}

internal fun List<ReservationResponse>.toDomains(): List<Reservation> = this.map { it.toDomain() }
