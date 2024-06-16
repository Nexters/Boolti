package com.nexters.boolti.presentation.screen.ticket.detail

import com.nexters.boolti.domain.model.LegacyTicket
import com.nexters.boolti.domain.model.TicketGroup

data class TicketDetailUiState(
    val legacyTicket: LegacyTicket = LegacyTicket(),
    val refundPolicy: List<String> = emptyList(),
    val ticketGroup: TicketGroup = TicketGroup(),
)
