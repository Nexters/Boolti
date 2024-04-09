package com.nexters.boolti.data.repository

import com.nexters.boolti.data.datasource.HostDataSource
import com.nexters.boolti.data.datasource.TicketDataSource
import com.nexters.boolti.domain.exception.ManagerCodeErrorType
import com.nexters.boolti.domain.exception.ManagerCodeException
import com.nexters.boolti.domain.extension.errorType
import com.nexters.boolti.domain.model.Ticket
import com.nexters.boolti.domain.repository.TicketRepository
import com.nexters.boolti.domain.request.ManagerCodeRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

internal class TicketRepositoryImpl @Inject constructor(
    private val dataSource: TicketDataSource,
    private val hostDataSource: HostDataSource,
) : TicketRepository {
    override suspend fun getTicket(): Flow<List<Ticket>> = flow {
        emit(dataSource.getTickets())
    }

    override suspend fun getTicket(ticketId: String): Flow<Ticket> = flow {
        emit(dataSource.getTicket(ticketId))
    }

    override suspend fun requestEntrance(request: ManagerCodeRequest): Flow<Boolean> = flow {
        val response = hostDataSource.requestEntranceWithManagerCode(request)
        if (response.isSuccessful) {
            emit(response.body() ?: false)
        } else {
            val errMsg = response.errorBody()?.string()
            throw ManagerCodeException(ManagerCodeErrorType.fromString(errMsg?.errorType))
        }
    }
}
