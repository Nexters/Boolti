package com.nexters.boolti.domain.repository

import com.nexters.boolti.domain.model.Ticket
import com.nexters.boolti.domain.request.ManagerCodeRequest
import kotlinx.coroutines.flow.Flow

interface TicketRepository {
    suspend fun getTicket(): Flow<List<Ticket>>
    suspend fun getTicket(ticketId: String): Flow<Ticket>
    suspend fun requestEntrance(request: ManagerCodeRequest): Flow<Boolean>
}
