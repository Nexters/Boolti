package com.nexters.boolti.data.repository

import com.nexters.boolti.data.datasource.TicketingDataSource
import com.nexters.boolti.domain.model.TicketWithQuantity
import com.nexters.boolti.domain.model.TicketingInfo
import com.nexters.boolti.domain.repository.TicketingRepository
import com.nexters.boolti.domain.request.SalesTicketRequest
import com.nexters.boolti.domain.request.TicketingInfoRequest
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
}
