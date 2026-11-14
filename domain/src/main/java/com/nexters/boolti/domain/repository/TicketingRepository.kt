package com.nexters.boolti.domain.repository

import com.nexters.boolti.domain.model.ApprovePaymentResponse
import com.nexters.boolti.domain.model.InviteCodeStatus
import com.nexters.boolti.domain.model.ReservationDetail
import com.nexters.boolti.domain.model.TicketWithQuantity
import com.nexters.boolti.domain.model.TicketingInfo
import com.nexters.boolti.domain.request.CheckInviteCodeRequest
import com.nexters.boolti.domain.request.OrderIdRequest
import com.nexters.boolti.domain.request.PaymentApproveRequest
import com.nexters.boolti.domain.request.PaymentCancelRequest
import com.nexters.boolti.domain.request.SalesTicketRequest
import com.nexters.boolti.domain.request.TicketingInfoRequest
import com.nexters.boolti.domain.request.TicketingRequest
import com.nexters.boolti.domain.model.PreQuestion
import com.nexters.boolti.domain.request.SubmitPreQuestionAnswersRequest
import kotlinx.coroutines.flow.Flow

interface TicketingRepository {
    fun getSalesTickets(request: SalesTicketRequest): Flow<List<TicketWithQuantity>>
    suspend fun getTicketingInfo(request: TicketingInfoRequest): Result<TicketingInfo>
    suspend fun requestReservation(request: TicketingRequest): Result<String>
    suspend fun checkInviteCode(request: CheckInviteCodeRequest): Result<InviteCodeStatus>
    fun getPaymentInfo(reservationId: String): Flow<ReservationDetail>
    suspend fun requestOrderId(request: OrderIdRequest): Result<String>
    fun approvePayment(request: PaymentApproveRequest): Flow<ApprovePaymentResponse>
    fun cancelPayment(request: PaymentCancelRequest): Flow<Boolean>
    suspend fun getPreQuestions(showId: String): Result<List<PreQuestion>>
    suspend fun submitPreQuestionAnswers(request: SubmitPreQuestionAnswersRequest): Result<Unit>
}
