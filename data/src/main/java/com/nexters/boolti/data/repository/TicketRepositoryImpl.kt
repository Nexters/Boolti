package com.nexters.boolti.data.repository

import com.nexters.boolti.data.datasource.TicketDataSource
import com.nexters.boolti.domain.model.Ticket
import com.nexters.boolti.domain.repository.TicketRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TicketRepositoryImpl @Inject constructor(
    private val dataSource: TicketDataSource,
) : TicketRepository {
    override suspend fun getTicket(): Flow<List<Ticket>> = flow {
        emit(dataSource.getTickets())
    }
}
