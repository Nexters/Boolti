package com.nexters.boolti.presentation.screen.ticket.detail

import com.nexters.boolti.domain.model.LegacyTicket
import com.nexters.boolti.domain.model.PreQuestionAnswer
import com.nexters.boolti.domain.model.TicketGroup
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.time.LocalDate

data class TicketDetailUiState(
    val legacyTicket: LegacyTicket = LegacyTicket(),
    val refundPolicy: List<String> = emptyList(),
    val ticketGroup: TicketGroup = TicketGroup(),
    val currentPage: Int = 0,
    val preQuestionAnswers: ImmutableList<PreQuestionAnswer> = persistentListOf(),
    val canEditPreQuestion: Boolean = false,
) {
    val isShowDate: Boolean = LocalDate.now() == ticketGroup.showDate.toLocalDate()
    val isRefundableGift: Boolean = ticketGroup.isGift &&
            LocalDate.now() < ticketGroup.showDate.toLocalDate()
}
