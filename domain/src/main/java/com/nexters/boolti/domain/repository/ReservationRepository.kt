package com.nexters.boolti.domain.repository

import com.nexters.boolti.domain.model.Reservation
import kotlinx.coroutines.flow.Flow

interface ReservationRepository {
    fun getReservations(): Flow<List<Reservation>>
}