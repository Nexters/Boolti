package com.nexters.boolti.domain.repository

import com.nexters.boolti.domain.model.InviteCodeStatus
import com.nexters.boolti.domain.model.ReservationDetail
import com.nexters.boolti.domain.model.TicketWithQuantity
import com.nexters.boolti.domain.model.TicketingInfo
import com.nexters.boolti.domain.request.CheckInviteCodeRequest
import com.nexters.boolti.domain.request.OrderIdRequest
import com.nexters.boolti.domain.request.PaymentApproveRequest
import com.nexters.boolti.domain.request.SalesTicketRequest
import com.nexters.boolti.domain.request.TicketingInfoRequest
import com.nexters.boolti.domain.request.TicketingRequest
import kotlinx.coroutines.flow.Flow

interface TicketingRepository {
    fun getSalesTickets(request: SalesTicketRequest): Flow<List<TicketWithQuantity>>
    fun getTicketingInfo(request: TicketingInfoRequest): Flow<TicketingInfo>
    fun requestReservation(request: TicketingRequest): Flow<String>
    fun checkInviteCode(request: CheckInviteCodeRequest): Flow<InviteCodeStatus>
    fun getPaymentInfo(reservationId: String): Flow<ReservationDetail>
    fun getOrderId(request: OrderIdRequest): Flow<String>
    fun approvePayment(request: PaymentApproveRequest): Flow<String>
}
