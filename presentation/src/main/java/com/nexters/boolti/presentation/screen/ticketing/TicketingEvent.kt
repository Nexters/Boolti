package com.nexters.boolti.presentation.screen.ticketing

sealed interface TicketingEvent {
    data class TicketingSuccess(val reservationId: String, val showId: String) : TicketingEvent
}
