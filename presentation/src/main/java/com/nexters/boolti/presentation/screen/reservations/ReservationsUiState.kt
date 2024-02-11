package com.nexters.boolti.presentation.screen.reservations

import com.nexters.boolti.domain.model.Reservation

data class ReservationsUiState(
    val reservations: List<Reservation> = emptyList()
)
