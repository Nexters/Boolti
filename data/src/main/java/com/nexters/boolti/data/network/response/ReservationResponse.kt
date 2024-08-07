package com.nexters.boolti.data.network.response

import com.nexters.boolti.data.util.toLocalDateTime
import com.nexters.boolti.data.util.toReservationState
import com.nexters.boolti.domain.model.Reservation
import kotlinx.serialization.Serializable

@Serializable
internal data class ReservationResponse(
    val reservationId: String,
    val reservationStatus: String,
    val reservationDate: String,
    val giftId: String?,
    val showName: String,
    val showImg: String,
    val salesTicketName: String,
    val ticketCount: Int,
    val ticketPrice: Int = 0,
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
