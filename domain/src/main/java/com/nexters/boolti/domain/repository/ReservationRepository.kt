package com.nexters.boolti.domain.repository

import com.nexters.boolti.domain.model.PreQuestionAnswer
import com.nexters.boolti.domain.model.Reservation
import com.nexters.boolti.domain.model.ReservationDetail
import com.nexters.boolti.domain.request.RefundRequest
import com.nexters.boolti.domain.request.SubmitPreQuestionAnswersRequest
import kotlinx.coroutines.flow.Flow

interface ReservationRepository {
    fun getReservations(): Flow<List<Reservation>>
    fun findReservationById(id: String): Flow<ReservationDetail>
    fun refund(request: RefundRequest): Flow<Unit>
    fun getPreQuestionAnswers(reservationId: String): Flow<List<PreQuestionAnswer>>
    fun updatePreQuestionAnswers(request: SubmitPreQuestionAnswersRequest): Flow<Unit>
}