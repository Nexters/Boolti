package com.nexters.boolti.presentation.screen.ticket.detail

import com.nexters.boolti.domain.model.LegacyTicket

data class TicketDetailUiState(
    val legacyTicket: LegacyTicket = LegacyTicket(),
    val refundPolicy: List<String> = emptyList(),
)
