package com.nexters.boolti.domain.repository

import com.nexters.boolti.domain.model.TicketWithQuantity
import com.nexters.boolti.domain.request.SalesTicketRequest
import kotlinx.coroutines.flow.Flow

interface TicketingRepository {
    fun getSalesTickets(request: SalesTicketRequest): Flow<List<TicketWithQuantity>>
}
