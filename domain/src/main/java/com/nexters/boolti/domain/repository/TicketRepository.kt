package com.nexters.boolti.domain.repository

import com.nexters.boolti.domain.model.LegacyTicket
import com.nexters.boolti.domain.model.TicketGroup
import com.nexters.boolti.domain.request.ManagerCodeRequest
import kotlinx.coroutines.flow.Flow

interface TicketRepository {
    suspend fun legacyGetTicket(): Flow<List<LegacyTicket>>
    suspend fun legacyGetTicket(ticketId: String): Flow<LegacyTicket>
    fun getTickets(): Flow<List<TicketGroup>>
    fun getTicket(reservationId: String): Flow<TicketGroup>
    suspend fun requestEntrance(request: ManagerCodeRequest): Flow<Boolean>
}
