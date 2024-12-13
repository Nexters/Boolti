package com.nexters.boolti.presentation.reservationdetail

import com.nexters.boolti.domain.model.ReservationDetail

sealed interface ReservationDetailUiState {
    data object Loading : ReservationDetailUiState

    data class Success(
        val reservation: ReservationDetail,
    ) : ReservationDetailUiState

    data class Error(
        val message: String = ""
    ) : ReservationDetailUiState
}
