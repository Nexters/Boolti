package com.nexters.boolti.domain.repository

import com.nexters.boolti.domain.model.TicketWithQuantity
import com.nexters.boolti.domain.model.TicketingInfo
import com.nexters.boolti.domain.request.SalesTicketRequest
import com.nexters.boolti.domain.request.TicketingInfoRequest
import com.nexters.boolti.domain.request.TicketingRequest
import kotlinx.coroutines.flow.Flow

interface TicketingRepository {
    fun getSalesTickets(request: SalesTicketRequest): Flow<List<TicketWithQuantity>>
    fun getTicketingInfo(request: TicketingInfoRequest): Flow<TicketingInfo>
    fun requestReservation(request: TicketingRequest): Flow<String>
}
