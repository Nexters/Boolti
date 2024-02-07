package com.nexters.boolti.presentation.screen.ticket

import com.nexters.boolti.domain.model.Ticket

data class TicketUiState(
    val tickets: List<Ticket> = emptyList(),
)
