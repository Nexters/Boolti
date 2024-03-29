package com.nexters.boolti.data.network.api

import com.nexters.boolti.data.network.response.ReservationDetailResponse
import com.nexters.boolti.data.network.response.ReservationResponse
import com.nexters.boolti.domain.request.RefundRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

internal interface ReservationService {
    @GET("/app/api/v1/reservations")
    suspend fun getReservations(): List<ReservationResponse>

    @GET("/app/api/v1/reservation/{reservationId}")
    suspend fun findReservationById(@Path("reservationId") reservationId: String): ReservationDetailResponse

    @PATCH("/app/api/v1/reservation/refund")
    suspend fun refund(@Body request: RefundRequest): Boolean
}
