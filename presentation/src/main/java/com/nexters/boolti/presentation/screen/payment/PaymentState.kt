package com.nexters.boolti.presentation.screen.payment

import com.nexters.boolti.domain.model.ReservationDetail

data class PaymentCompleteState(
    val loading: Boolean = false,
    val reservationDetail: ReservationDetail? = null
)
