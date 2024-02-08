package com.nexters.boolti.domain.repository

import com.nexters.boolti.domain.model.Ticket
import kotlinx.coroutines.flow.Flow

interface TicketRepository {
    suspend fun getTicket(): Flow<List<Ticket>>
}
