package com.nexters.boolti.presentation.screen.reservationdetail

import com.nexters.boolti.domain.model.ReservationDetail
import com.nexters.boolti.domain.model.ReservationState
import java.time.LocalDateTime

sealed interface ReservationDetailUiState {
    data object Loading : ReservationDetailUiState

    data class Success(
        val reservation: ReservationDetail,
        val canShowPreQuestions: Boolean,
        val canEditPreQuestions: Boolean,
    ) : ReservationDetailUiState

    data class Error(
        val message: String = ""
    ) : ReservationDetailUiState
}

internal fun ReservationDetail.canShowPreQuestions(): Boolean {
    return reservationState !in listOf(
        ReservationState.CANCELED,
        ReservationState.REFUNDED,
    )
}

internal fun ReservationDetail.canEditPreQuestions(now: LocalDateTime = LocalDateTime.now()): Boolean {
    return canShowPreQuestions() && salesEndDateTime >= now
}
