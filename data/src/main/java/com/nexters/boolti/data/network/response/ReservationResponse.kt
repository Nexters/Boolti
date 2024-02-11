package com.nexters.boolti.data.network.response

import com.nexters.boolti.data.util.toLocalDateTime
import com.nexters.boolti.domain.model.Reservation
import com.nexters.boolti.domain.model.ReservationState
import kotlinx.serialization.Serializable

@Serializable
data class ReservationResponse(
    val reservationId: String,
    val reservationStatus: String,
    val reservationDate: String,
    val showName: String,
    val showImg: String,
    val salesTicketName: String,
    val ticketCount: Int,
    val ticketPrice: Int,
) {
    fun toDomain(): Reservation {
        val reservationState = when (reservationStatus) {
            "WAITING_FOR_DEPOSIT" -> ReservationState.DEPOSITING
            "CANCELLED" -> ReservationState.CANCELED
            "RESERVATION_COMPLETED" -> ReservationState.RESERVED
            "WAITING_FOR_REFUND" -> ReservationState.REFUNDING
            "REFUND_COMPLETED" -> ReservationState.REFUNDED
            else -> ReservationState.UNDEFINED
        }

        return Reservation(
            id = reservationId,
            reservationState = reservationState,
            reservationDateTime = reservationDate.toLocalDateTime(),
            showName = showName,
            showImage = showImg,
            salesTicketName = salesTicketName,
            ticketCount = ticketCount,
            ticketPrice = ticketPrice,
        )
    }
}

fun List<ReservationResponse>.toDomains(): List<Reservation> = this.map { it.toDomain() }