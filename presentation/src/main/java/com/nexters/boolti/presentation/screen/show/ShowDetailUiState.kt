package com.nexters.boolti.presentation.screen.show

import com.nexters.boolti.domain.model.ShowDetail
import com.nexters.boolti.domain.model.TicketingTicket

data class ShowDetailUiState(
    val showDetail: ShowDetail = ShowDetail(),
    val tickets: List<TicketingTicket> = emptyList(),
    val leftAmount: Map<String, Int> = emptyMap(),
    val purchased: Boolean = false,
)