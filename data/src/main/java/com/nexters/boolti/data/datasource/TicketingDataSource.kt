package com.nexters.boolti.data.datasource

import com.nexters.boolti.data.network.api.TicketingService
import com.nexters.boolti.data.network.request.ReservationInviteTicketRequest
import com.nexters.boolti.data.network.request.ReservationSalesTicketRequest
import com.nexters.boolti.data.network.response.ApprovePaymentResponse
import com.nexters.boolti.data.network.response.CheckInviteCodeResponse
import com.nexters.boolti.data.network.response.ReservationDto
import com.nexters.boolti.domain.model.TicketWithQuantity
import com.nexters.boolti.domain.model.TicketingInfo
import com.nexters.boolti.domain.request.CheckInviteCodeRequest
import com.nexters.boolti.domain.request.OrderIdRequest
import com.nexters.boolti.domain.request.PaymentApproveRequest
import com.nexters.boolti.domain.request.PaymentCancelRequest
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

    suspend fun requestReservationSalesTicket(request: ReservationSalesTicketRequest): Response<ReservationDto> {
        return ticketingService.requestReservationSalesTicket(request)
    }

    suspend fun requestReservationInviteTicket(request: ReservationInviteTicketRequest): Response<ReservationDto> {
        return ticketingService.requestReservationInviteTicket(request)
    }

    suspend fun checkInviteCode(request: CheckInviteCodeRequest): Response<CheckInviteCodeResponse> {
        return ticketingService.checkInviteCode(
            showId = request.showId,
            salesTicketId = request.salesTicketId,
            code = request.inviteCode,
        )
    }

    suspend fun requestOrderId(request: OrderIdRequest): String {
        return ticketingService.requestOrderId(request).orderId
    }

    suspend fun approvePayment(request: PaymentApproveRequest): Response<ApprovePaymentResponse> {
        return ticketingService.approvePayment(request)
    }

    suspend fun cancelPayment(request: PaymentCancelRequest): Boolean {
        val response = ticketingService.cancelPayment(request)
        return response.isSuccessful && response.body() ?: false
    }
}
