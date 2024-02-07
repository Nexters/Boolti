package com.nexters.boolti.data.datasource

import com.nexters.boolti.data.network.api.TicketingService
import com.nexters.boolti.domain.model.TicketWithQuantity
import com.nexters.boolti.domain.request.SalesTicketRequest
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TicketingDataSource @Inject constructor(
    private val ticketingService: TicketingService,
) {
    suspend fun getSalesTickets(request: SalesTicketRequest): List<TicketWithQuantity> {
        return ticketingService.getSalesTickets(request.showId).map {
            it.toDomain()
        }
    }
}
