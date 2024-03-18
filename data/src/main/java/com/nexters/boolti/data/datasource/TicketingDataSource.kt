package com.nexters.boolti.data.datasource

import com.nexters.boolti.data.network.api.TicketingService
import com.nexters.boolti.data.network.request.ReservationInviteTicketRequest
import com.nexters.boolti.data.network.request.ReservationSalesTicketRequest
import com.nexters.boolti.data.network.response.CheckInviteCodeResponse
import com.nexters.boolti.domain.model.TicketWithQuantity
import com.nexters.boolti.domain.model.TicketingInfo
import com.nexters.boolti.domain.request.CheckInviteCodeRequest
import com.nexters.boolti.domain.request.SalesTicketRequest
import com.nexters.boolti.domain.request.TicketingInfoRequest
import retrofit2.Response
import javax.inject.Inject

internal class TicketingDataSource @Inject constructor(
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

    suspend fun requestReservationSalesTicket(request: ReservationSalesTicketRequest): String {
        return ticketingService.requestReservationSalesTicket(request).reservationId
    }

    suspend fun requestReservationInviteTicket(request: ReservationInviteTicketRequest): String {
        return ticketingService.requestReservationInviteTicket(request).reservationId
    }

    suspend fun checkInviteCode(request: CheckInviteCodeRequest): Response<CheckInviteCodeResponse> {
        return ticketingService.checkInviteCode(
            showId = request.showId,
            salesTicketId = request.salesTicketId,
            code = request.inviteCode,
        )
    }
}
