package com.nexters.boolti.domain.model

import java.time.LocalDateTime

data class Reservation(
    val id: String,
    val reservationState: ReservationState,
    val reservationDateTime: LocalDateTime,
    val showName: String,
    val showImage: String,
    val salesTicketName: String,
    val ticketCount: Int,
    val ticketPrice: Int,
)
