package com.nexters.boolti.data.repository

import com.nexters.boolti.data.cache.CacheKeys
import com.nexters.boolti.data.cache.CacheStore
import com.nexters.boolti.data.datasource.ReservationDataSource
import com.nexters.boolti.data.datasource.TicketingDataSource
import com.nexters.boolti.data.network.request.toData
import com.nexters.boolti.domain.exception.TicketingErrorType
import com.nexters.boolti.domain.exception.TicketingException
import com.nexters.boolti.domain.extension.errorType
import com.nexters.boolti.domain.model.ApprovePaymentResponse
import com.nexters.boolti.domain.model.InviteCodeStatus
import com.nexters.boolti.domain.model.PreQuestion
import com.nexters.boolti.domain.model.ReservationDetail
import com.nexters.boolti.domain.model.TicketWithQuantity
import com.nexters.boolti.domain.model.TicketingInfo
import com.nexters.boolti.domain.repository.TicketingRepository
import com.nexters.boolti.domain.request.CheckInviteCodeRequest
import com.nexters.boolti.domain.request.OrderIdRequest
import com.nexters.boolti.domain.request.PaymentApproveRequest
import com.nexters.boolti.domain.request.PaymentCancelRequest
import com.nexters.boolti.domain.request.SalesTicketRequest
import com.nexters.boolti.domain.request.TicketingInfoRequest
import com.nexters.boolti.domain.request.TicketingRequest
import com.nexters.boolti.domain.request.SubmitPreQuestionAnswersRequest
import com.nexters.boolti.domain.util.suspendRunCatching
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

internal class TicketingRepositoryImpl @Inject constructor(
    private val dataSource: TicketingDataSource,
    private val reservationDataSource: ReservationDataSource,
    private val cacheStore: CacheStore,
) : TicketingRepository {
    override fun getSalesTickets(request: SalesTicketRequest): Flow<List<TicketWithQuantity>> = flow {
        emit(dataSource.getSalesTickets(request))
    }

    override suspend fun getTicketingInfo(request: TicketingInfoRequest): Result<TicketingInfo> = suspendRunCatching {
        dataSource.getTicketingInfo(request)
    }

    override suspend fun requestReservation(request: TicketingRequest): Result<String> = suspendRunCatching {
        val response = when (request) {
            is TicketingRequest.Invite -> dataSource.requestReservationInviteTicket(request.toData())
            is TicketingRequest.Free -> dataSource.requestReservationSalesTicket(request.toData())
        }
        if (!response.isSuccessful) {
            val errMsg = response.errorBody()?.string()
            throw TicketingException(TicketingErrorType.fromString(errMsg?.errorType))
        }
        checkNotNull(response.body()?.reservationId) { "예매 응답에 reservationId가 없습니다." }
    }

    override suspend fun checkInviteCode(request: CheckInviteCodeRequest): Result<InviteCodeStatus> = suspendRunCatching {
        val response = dataSource.checkInviteCode(request)
        when {
            !response.isSuccessful -> InviteCodeStatus.fromString(response.errorBody()?.string()?.errorType)
            response.body()?.isUsed?.not() == true -> InviteCodeStatus.Valid
            else -> InviteCodeStatus.Duplicated
        }
    }

    override fun getPaymentInfo(reservationId: String): Flow<ReservationDetail> = flow {
        emit(reservationDataSource.findReservationById(reservationId).toDomain())
    }

    override suspend fun requestOrderId(request: OrderIdRequest): Result<String> = suspendRunCatching {
        dataSource.requestOrderId(request)
    }

    override fun approvePayment(request: PaymentApproveRequest): Flow<ApprovePaymentResponse> = flow {
        val response = dataSource.approvePayment(request)
        if (response.isSuccessful) {
            val body = response.body() ?: throw TicketingException(TicketingErrorType.Unknown)
            emit(body.toDomain())
        } else {
            val errMsg = response.errorBody()?.string()
            throw TicketingException(TicketingErrorType.fromString(errMsg?.errorType))
        }
    }

    override fun cancelPayment(request: PaymentCancelRequest): Flow<Boolean> = flow {
        emit(dataSource.cancelPayment(request))
    }

    override suspend fun getPreQuestions(showId: String): Result<List<PreQuestion>> = suspendRunCatching {
        cacheStore.getOrFetch(CacheKeys.preQuestions(showId)) { dataSource.getPreQuestions(showId) }
    }

    override suspend fun submitPreQuestionAnswers(request: SubmitPreQuestionAnswersRequest): Result<Unit> = suspendRunCatching {
        dataSource.submitPreQuestionAnswers(request.toData())
    }
}
