package com.nexters.boolti.data.repository

import com.nexters.boolti.data.datasource.HostDataSource
import com.nexters.boolti.data.datasource.TicketDataSource
import com.nexters.boolti.domain.exception.ManagerCodeErrorType
import com.nexters.boolti.domain.exception.ManagerCodeException
import com.nexters.boolti.domain.exception.TicketException
import com.nexters.boolti.domain.extension.errorType
import com.nexters.boolti.domain.model.LegacyTicket
import com.nexters.boolti.domain.model.TicketGroup
import com.nexters.boolti.domain.repository.TicketRepository
import com.nexters.boolti.domain.request.ManagerCodeRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

internal class TicketRepositoryImpl @Inject constructor(
    private val dataSource: TicketDataSource,
    private val hostDataSource: HostDataSource,
) : TicketRepository {
    override suspend fun legacyGetTicket(): Flow<List<LegacyTicket>> = flow {
        emit(dataSource.legacyGetTickets())
    }

    override suspend fun legacyGetTicket(ticketId: String): Flow<LegacyTicket> = flow {
        val response = dataSource.legacyGetTicket(ticketId)
        if (response.isSuccessful) {
            response.body()?.toDomain()?.let { emit(it) }
        } else {
            when (response.code()) {
                404 -> throw TicketException.TicketNotFound
            }
        }
    }

    override fun getTickets(): Flow<List<TicketGroup>> = flow {
        emit(dataSource.getTickets())
    }

    override fun getTicket(reservationId: String): Flow<TicketGroup> = flow {
        val response = dataSource.getTicket(reservationId)
        if (response.isSuccessful) {
            response.body()?.toDomain()?.let { emit(it) }
        } else {
            when (response.code()) {
                404 -> throw TicketException.TicketNotFound
            }
        }
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
