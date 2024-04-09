package com.nexters.boolti.data.network.api

import com.nexters.boolti.data.network.response.TicketDetailDto
import com.nexters.boolti.data.network.response.TicketDto
import retrofit2.http.GET
import retrofit2.http.Path

internal interface TicketService {
    @GET("/app/api/v1/tickets")
    suspend fun getTickets(): List<TicketDto>

    @GET("/app/api/v1/ticket/{ticketId}")
    suspend fun getTicket(
        @Path("ticketId") ticketId: String,
    ): TicketDetailDto
}
