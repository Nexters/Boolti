package com.nexters.boolti.presentation.screen.reservations

import com.nexters.boolti.domain.model.ReservationDetail

sealed interface ReservationDetailUiState {
    data object Loading : ReservationDetailUiState

    data class Success(
        val reservation: ReservationDetail,
        val refundPolicy: List<String> = emptyList(),
    ) : ReservationDetailUiState

    data class Error(
        val message: String = ""
    ) : ReservationDetailUiState
}