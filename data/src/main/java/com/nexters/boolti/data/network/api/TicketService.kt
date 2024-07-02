package com.nexters.boolti.data.network.api

import com.nexters.boolti.data.network.response.TicketDetailDto
import com.nexters.boolti.data.network.response.TicketDto
import com.nexters.boolti.data.network.response.TicketGroupDto
import com.nexters.boolti.data.network.response.TicketsDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

internal interface TicketService {
    @GET("/app/api/v1/tickets")
    suspend fun legacyGetTickets(): List<TicketDto>

    @GET("/app/api/v1/ticket/{ticketId}")
    suspend fun legacyGetTicket(
        @Path("ticketId") ticketId: String,
    ): Response<TicketDetailDto>

    @GET("/app/api/v1/reservation/tickets")
    suspend fun getTickets(): List<TicketsDto>

    @GET("/app/api/v1/ticket/reservation/{reservationId}")
    suspend fun getTicket(
        @Path("reservationId") reservationId: String,
    ): Response<TicketGroupDto>
}
