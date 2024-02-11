package com.nexters.boolti.data.repository

import com.nexters.boolti.data.datasource.ReservationDataSource
import com.nexters.boolti.data.network.response.toDomains
import com.nexters.boolti.domain.model.Reservation
import com.nexters.boolti.domain.repository.ReservationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ReservationRepositoryImpl @Inject constructor(
    private val reservationDataSource: ReservationDataSource,
): ReservationRepository {
    override fun getReservations(): Flow<List<Reservation>> = flow {
        emit(reservationDataSource.getReservations().toDomains())
    }
}