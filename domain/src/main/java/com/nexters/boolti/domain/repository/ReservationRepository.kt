package com.nexters.boolti.domain.repository

import com.nexters.boolti.domain.model.Reservation
import com.nexters.boolti.domain.model.ReservationDetail
import com.nexters.boolti.domain.request.RefundRequest
import kotlinx.coroutines.flow.Flow

interface ReservationRepository {
    fun getReservations(): Flow<List<Reservation>>
    fun findReservationById(id: String): Flow<ReservationDetail>
    fun refund(request: RefundRequest): Flow<Unit>
}