package com.nexters.boolti.data.datasource

import com.nexters.boolti.data.network.api.TicketService
import com.nexters.boolti.domain.model.Ticket
import com.nexters.boolti.domain.request.QrScanRequest
import retrofit2.Response
import javax.inject.Inject

class TicketDataSource @Inject constructor(
    private val apiService: TicketService,
) {
    suspend fun getTickets(): List<Ticket> = apiService.getTickets().map { it.toDomain() }
    suspend fun getTicket(ticketId: String): Ticket = apiService.getTicket(ticketId).toDomain()
    suspend fun requestEntrance(request: QrScanRequest): Response<Boolean> = apiService.requestEntrance(request)
}
