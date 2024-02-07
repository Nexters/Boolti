package com.nexters.boolti.data.network.api

import com.nexters.boolti.data.network.response.SalesTicketTypeDto
import retrofit2.http.GET
import retrofit2.http.Path

interface TicketingService {
    @GET("app/api/v1/sales-ticket-type/{showId}")
    suspend fun getSalesTickets(
        @Path("showId") showId: String,
    ): List<SalesTicketTypeDto>
}
