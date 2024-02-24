package com.nexters.boolti.presentation.screen.ticket

import com.nexters.boolti.domain.model.Ticket

data class TicketUiState(
    val loading: Boolean = false,
    val tickets: List<Ticket> = emptyList(),
)
