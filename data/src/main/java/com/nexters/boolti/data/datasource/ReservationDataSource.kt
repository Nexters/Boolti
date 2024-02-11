package com.nexters.boolti.data.datasource

import com.nexters.boolti.data.network.api.ReservationService
import com.nexters.boolti.data.network.response.ReservationDetailResponse
import com.nexters.boolti.data.network.response.ReservationResponse
import javax.inject.Inject

class ReservationDataSource @Inject constructor(
    private val reservationService: ReservationService,
) {
    suspend fun getReservations(): List<ReservationResponse> = reservationService.getReservations()

    suspend fun findReservationById(id: String): ReservationDetailResponse =
        reservationService.findReservationById(id)
}