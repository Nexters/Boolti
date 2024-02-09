package com.nexters.boolti.data.network.api

import com.nexters.boolti.data.network.response.TicketDetailDto
import com.nexters.boolti.data.network.response.TicketDto
import com.nexters.boolti.domain.request.QrScanRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TicketService {
    @GET("/app/api/v1/tickets")
    suspend fun getTickets(): List<TicketDto>

    @GET("/app/api/v1/ticket/{ticketId}")
    suspend fun getTicket(
        @Path("ticketId") ticketId: String,
    ): TicketDetailDto

    @POST("/app/api/v1/ticket/entrance")
    suspend fun requestEntrance(
        @Body qrScanRequest: QrScanRequest,
    ): Response<Boolean>
}
