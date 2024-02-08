package com.nexters.boolti.presentation.screen.ticket

import com.nexters.boolti.domain.model.Ticket

data class TicketUiState(
    // TODO 로딩 중 어떻게 표시할 지 결정되면 Loading 필드 추가.
    val tickets: List<Ticket> = emptyList(),
)
