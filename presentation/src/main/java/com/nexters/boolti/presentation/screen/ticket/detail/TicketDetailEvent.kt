package com.nexters.boolti.presentation.screen.ticket.detail

sealed interface TicketDetailEvent {
    data object ManagerCodeValid : TicketDetailEvent
    data object OnRefresh : TicketDetailEvent
    data object NotFound : TicketDetailEvent
    data object GiftRefunded : TicketDetailEvent
    data object FailedToRefund : TicketDetailEvent
    data object NetworkError : TicketDetailEvent
}
