package com.nexters.boolti.presentation.screen.ticket.detail

import com.nexters.boolti.domain.model.LegacyTicket
import com.nexters.boolti.domain.model.TicketGroup
import java.time.LocalDate

data class TicketDetailUiState(
    val legacyTicket: LegacyTicket = LegacyTicket(),
    val refundPolicy: List<String> = emptyList(),
    val ticketGroup: TicketGroup = TicketGroup(),
    val currentPage: Int = 0,
) {
    val isShowDate: Boolean = LocalDate.now() == ticketGroup.showDate.toLocalDate()
    val isRefundableGift: Boolean = ticketGroup.isGift &&
            LocalDate.now() < ticketGroup.showDate.toLocalDate()
}
