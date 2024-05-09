package com.nexters.boolti.presentation.screen.ticketing

sealed interface TicketingEvent {
    data class TicketingSuccess(val reservationId: String, val showId: String) : TicketingEvent
    data class ProgressPayment(val userId: String, val orderId: String) : TicketingEvent
    data object NoRemainingQuantity : TicketingEvent
}
