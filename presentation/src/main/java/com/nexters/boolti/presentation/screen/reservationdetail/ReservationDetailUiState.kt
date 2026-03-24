package com.nexters.boolti.presentation.screen.reservationdetail

import com.nexters.boolti.domain.model.ReservationDetail
import com.nexters.boolti.domain.model.ReservationState
import java.time.LocalDateTime

sealed interface ReservationDetailUiState {
    data object Loading : ReservationDetailUiState

    data class Success(
        val reservation: ReservationDetail,
        val canEditPreQuestions: Boolean,
    ) : ReservationDetailUiState

    data class Error(
        val message: String = ""
    ) : ReservationDetailUiState
}

internal fun ReservationDetail.canEditPreQuestions(now: LocalDateTime = LocalDateTime.now()): Boolean {
    return salesEndDateTime >= now && reservationState !in listOf(
        ReservationState.CANCELED,
        ReservationState.REFUNDED,
    )
}
