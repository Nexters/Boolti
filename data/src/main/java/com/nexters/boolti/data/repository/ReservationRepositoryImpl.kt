package com.nexters.boolti.data.repository

import com.nexters.boolti.data.datasource.ReservationDataSource
import com.nexters.boolti.data.network.response.toDomains
import com.nexters.boolti.domain.model.Reservation
import com.nexters.boolti.domain.model.ReservationDetail
import com.nexters.boolti.domain.repository.ReservationRepository
import com.nexters.boolti.domain.request.RefundRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

internal class ReservationRepositoryImpl @Inject constructor(
    private val reservationDataSource: ReservationDataSource,
) : ReservationRepository {
    override fun getReservations(): Flow<List<Reservation>> = flow {
        emit(reservationDataSource.getReservations().toDomains())
    }

    override fun findReservationById(id: String): Flow<ReservationDetail> = flow {
        emit(reservationDataSource.findReservationById(id).toDomain())
    }

    override fun refund(request: RefundRequest): Flow<Unit> = flow {
        val isSuccessful = reservationDataSource.refund(request)
        if (isSuccessful) {
            emit(Unit)
        } else {
            throw RuntimeException("취소 실패")
        }
    }
}
