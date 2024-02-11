package com.nexters.boolti.data.network.api

import com.nexters.boolti.data.network.response.ReservationResponse
import retrofit2.http.GET

interface ReservationService {
    @GET("/app/api/v1/reservations")
    suspend fun getReservations(): List<ReservationResponse>
}