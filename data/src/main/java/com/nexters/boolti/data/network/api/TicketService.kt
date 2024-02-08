package com.nexters.boolti.data.network.api

import com.nexters.boolti.data.network.response.TicketDto
import retrofit2.http.GET

interface TicketService {
    @GET("/app/api/v1/tickets")
    suspend fun getTickets(): List<TicketDto>
}
