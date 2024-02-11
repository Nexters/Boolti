package com.nexters.boolti.data.datasource

import com.nexters.boolti.data.network.api.TicketingService
import com.nexters.boolti.domain.model.TicketWithQuantity
import com.nexters.boolti.domain.model.TicketingInfo
import com.nexters.boolti.domain.request.SalesTicketRequest
import com.nexters.boolti.domain.request.TicketingInfoRequest
import javax.inject.Inject

class TicketingDataSource @Inject constructor(
    private val ticketingService: TicketingService,
) {
    suspend fun getSalesTickets(request: SalesTicketRequest): List<TicketWithQuantity> {
        return ticketingService.getSalesTickets(request.showId).map {
            it.toDomain()
        }
    }

    suspend fun getTicketingInfo(request: TicketingInfoRequest): TicketingInfo {
        return ticketingService.getTicketingInfo(
            request.showId,
            request.salesTicketId,
            request.ticketCount,
        ).toDomain()
    }
}
