package com.nexters.boolti.data.network.api

import com.nexters.boolti.data.network.request.ReservationSalesTicketRequest
import com.nexters.boolti.data.network.response.CheckInviteCodeResponse
import com.nexters.boolti.data.network.response.ReservationDto
import com.nexters.boolti.data.network.response.SalesTicketTypeDto
import com.nexters.boolti.data.network.response.TicketingInfoDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
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

    @POST("/app/api/v1/reservation/sales-ticket")
    suspend fun requestReservationSalesTicket(
        @Body request: ReservationSalesTicketRequest,
    ): ReservationDto

    @GET("/app/api/v1/check/invite-code")
    suspend fun checkInviteCode(
        @Query("showId") showId: String,
        @Query("salesTicketTypeId") salesTicketId: String,
        @Query("inviteCode") code: String,
    ): Response<CheckInviteCodeResponse>
}
