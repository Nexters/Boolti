package com.nexters.boolti.presentation.screen.show

import com.nexters.boolti.domain.model.ShowDetail
import com.nexters.boolti.domain.model.TicketingTicket

data class ShowDetailUiState(
    val showDetail: ShowDetail = ShowDetail(),
    val purchased: Boolean = false,
)