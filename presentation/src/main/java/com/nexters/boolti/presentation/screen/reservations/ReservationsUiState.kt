package com.nexters.boolti.presentation.screen.reservations

import com.nexters.boolti.domain.model.Reservation

sealed interface ReservationsUiState {
    data object Loading : ReservationsUiState
    data object Error : ReservationsUiState

    data class Success(
        val reservations: List<Reservation> = emptyList()
    ) : ReservationsUiState
}
