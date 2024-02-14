package com.nexters.boolti.presentation.screen.payment

import com.nexters.boolti.domain.model.ReservationDetail

sealed interface PaymentState {
    data object Loading : PaymentState
    data class Success(val reservationDetail: ReservationDetail) : PaymentState
}
