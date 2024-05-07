package com.nexters.boolti.domain.exception

sealed class TicketException(override val message: String) : IllegalStateException(message) {
    data object TicketNotFound : TicketException("티켓이 존재하지 않습니다")
}
