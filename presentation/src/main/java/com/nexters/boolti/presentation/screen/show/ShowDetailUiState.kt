package com.nexters.boolti.presentation.screen.show

import com.nexters.boolti.domain.model.TicketingTicket

data class ShowDetailUiState(
    val tickets: List<TicketingTicket> = emptyList(),
    val leftAmount: Map<String, Int> = emptyMap(),
)