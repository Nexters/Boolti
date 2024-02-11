package com.nexters.boolti.data.network.api

import com.nexters.boolti.data.network.response.SalesTicketTypeDto
import com.nexters.boolti.data.network.response.TicketingInfoDto
import com.nexters.boolti.domain.request.TicketingInfoRequest
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TicketingService {
    @GET("app/api/v1/sales-ticket-type/{showId}")
    suspend fun getSalesTickets(
        @Path("showId") showId: String,
    ): List<SalesTicketTypeDto>

    @GET("/app/api/v1/reservation/payment-info")
    suspend fun getTicketingInfo(
        @Query("showId") showId: String,
        @Query("salesTicketTypeId") ticketId: String,
        @Query("ticketCount") ticketCount: Int,
    ): TicketingInfoDto
}
