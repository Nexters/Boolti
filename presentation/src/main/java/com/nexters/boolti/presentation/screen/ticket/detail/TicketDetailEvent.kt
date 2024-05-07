package com.nexters.boolti.presentation.screen.ticket.detail

sealed interface TicketDetailEvent {
    data object ManagerCodeValid : TicketDetailEvent
    data object OnRefresh : TicketDetailEvent
    data object NotFound : TicketDetailEvent
}
