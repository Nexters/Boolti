package com.nexters.boolti.data.datasource

import com.nexters.boolti.data.network.api.TicketService
import com.nexters.boolti.data.network.response.TicketDetailDto
import com.nexters.boolti.data.network.response.TicketGroupDto
import com.nexters.boolti.domain.model.LegacyTicket
import com.nexters.boolti.domain.model.TicketGroup
import retrofit2.Response
import javax.inject.Inject

internal class TicketDataSource @Inject constructor(
    private val apiService: TicketService,
) {
    suspend fun legacyGetTickets(): List<LegacyTicket> = apiService.legacyGetTickets().map { it.toDomain() }
    suspend fun legacyGetTicket(ticketId: String): Response<TicketDetailDto> = apiService.legacyGetTicket(ticketId)
    suspend fun getTickets(): List<TicketGroup> = apiService.getTickets().map { it.toDomain() }
    suspend fun getTicket(reservationId: String): Response<TicketGroupDto> = apiService.getTicket(reservationId)
}
