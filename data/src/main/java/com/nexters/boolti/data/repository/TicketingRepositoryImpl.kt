package com.nexters.boolti.data.repository

import com.nexters.boolti.data.datasource.ReservationDataSource
import com.nexters.boolti.data.datasource.TicketingDataSource
import com.nexters.boolti.data.network.request.toData
import com.nexters.boolti.domain.exception.TicketingErrorType
import com.nexters.boolti.domain.exception.TicketingException
import com.nexters.boolti.domain.extension.errorType
import com.nexters.boolti.domain.model.ApprovePaymentResponse
import com.nexters.boolti.domain.model.InviteCodeStatus
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

internal class TicketingRepositoryImpl @Inject constructor(
    private val dataSource: TicketingDataSource,
    private val reservationDataSource: ReservationDataSource,
) : TicketingRepository {
    override fun getSalesTickets(request: SalesTicketRequest): Flow<List<TicketWithQuantity>> = flow {
        emit(dataSource.getSalesTickets(request))
    }

    override fun getTicketingInfo(request: TicketingInfoRequest): Flow<TicketingInfo> = flow {
        emit(dataSource.getTicketingInfo(request))
    }

    override fun requestReservation(request: TicketingRequest): Flow<String> = flow {
        val response = when (request) {
            is TicketingRequest.Invite -> dataSource.requestReservationInviteTicket(request.toData())
            is TicketingRequest.Free -> dataSource.requestReservationSalesTicket(request.toData())
        }
        if (response.isSuccessful) {
            response.body()?.reservationId?.let { emit(it) }
        } else {
            val errMsg = response.errorBody()?.string()
            throw TicketingException(TicketingErrorType.fromString(errMsg?.errorType))
        }
    }

    override fun checkInviteCode(request: CheckInviteCodeRequest): Flow<InviteCodeStatus> = flow {
        val response = dataSource.checkInviteCode(request)
        if (response.isSuccessful) {
            if (response.body()?.isUsed?.not() == true) {
                emit(InviteCodeStatus.Valid)
            } else {
                emit(InviteCodeStatus.Duplicated)
            }
        } else {
            val errMsg = response.errorBody()?.string()
            val status = InviteCodeStatus.fromString(errMsg?.errorType)
            emit(status)
        }
    }

    override fun getPaymentInfo(reservationId: String): Flow<ReservationDetail> = flow {
        emit(reservationDataSource.findReservationById(reservationId).toDomain())
    }

    override fun requestOrderId(request: OrderIdRequest): Flow<String> = flow {
        emit(dataSource.requestOrderId(request))
    }

    override fun approvePayment(request: PaymentApproveRequest): Flow<ApprovePaymentResponse> = flow {
        val response = dataSource.approvePayment(request)
        if (response.isSuccessful) {
            response.body()?.let { emit(it.toDomain()) }
        } else {
            val errMsg = response.errorBody()?.string()
            throw TicketingException(TicketingErrorType.fromString(errMsg?.errorType))
        }
    }

    override fun cancelPayment(request: PaymentCancelRequest): Flow<Boolean> = flow {
        emit(dataSource.cancelPayment(request))
    }
}
