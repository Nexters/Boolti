package com.nexters.boolti.tosspayments

sealed interface PaymentEvent {
    data class Approved(
        val orderId: String,
        val reservationId: String,
    ) : PaymentEvent
}
