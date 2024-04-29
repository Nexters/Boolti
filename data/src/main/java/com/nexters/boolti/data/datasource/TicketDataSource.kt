package com.nexters.boolti.data.datasource

import com.nexters.boolti.data.network.api.TicketService
import com.nexters.boolti.data.network.response.TicketDetailDto
import com.nexters.boolti.domain.model.Ticket
import retrofit2.Response
import javax.inject.Inject

internal class TicketDataSource @Inject constructor(
    private val apiService: TicketService,
) {
    suspend fun getTickets(): List<Ticket> = apiService.getTickets().map { it.toDomain() }
    suspend fun getTicket(ticketId: String): Response<TicketDetailDto> = apiService.getTicket(ticketId)
}
