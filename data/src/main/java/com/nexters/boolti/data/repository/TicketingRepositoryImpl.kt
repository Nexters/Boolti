package com.nexters.boolti.data.repository

import com.nexters.boolti.data.datasource.TicketingDataSource
import com.nexters.boolti.data.network.request.toData
import com.nexters.boolti.domain.exception.InviteCodeException
import com.nexters.boolti.domain.extension.errorType
import com.nexters.boolti.domain.model.InviteCodeStatus
import com.nexters.boolti.domain.model.TicketWithQuantity
import com.nexters.boolti.domain.model.TicketingInfo
import com.nexters.boolti.domain.repository.TicketingRepository
import com.nexters.boolti.domain.request.CheckInviteCodeRequest
import com.nexters.boolti.domain.request.SalesTicketRequest
import com.nexters.boolti.domain.request.TicketingInfoRequest
import com.nexters.boolti.domain.request.TicketingRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TicketingRepositoryImpl @Inject constructor(
    private val dataSource: TicketingDataSource,
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
            is TicketingRequest.Normal -> dataSource.requestReservationSalesTicket(request.toData())
        }
        emit(response)
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
            throw InviteCodeException(status)
        }
    }
}
