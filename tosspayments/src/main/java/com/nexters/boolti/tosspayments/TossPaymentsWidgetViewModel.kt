package com.nexters.boolti.tosspayments

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.model.PaymentType
import com.nexters.boolti.domain.repository.TicketingRepository
import com.nexters.boolti.domain.request.PaymentApproveRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TossPaymentsWidgetViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val ticketingRepository: TicketingRepository,
) : ViewModel() {
    private val showId: String = savedStateHandle["extraKeyShowId"] ?: ""
    private val salesTicketTypeId: String = savedStateHandle["extraKeySalesTicketId"] ?: ""
    private val ticketCount: Int = savedStateHandle["extraKeyTicketCount"] ?: 1
    private val reservationName: String = savedStateHandle["extraKeyReservationName"] ?: ""
    private val reservationPhoneNumber: String = savedStateHandle["extraKeyReservationPhoneNumber"] ?: ""
    private val depositorName: String = savedStateHandle["extraKeyDepositorName"] ?: ""
    private val depositorPhoneNumber: String = savedStateHandle["extraKeyDepositorPhoneNumber"] ?: ""

    private val _event = Channel<PaymentEvent>()
    val event = _event.receiveAsFlow()

    fun approvePayment(
        orderId: String,
        paymentKey: String,
        totalPrice: Int,
    ) {
        viewModelScope.launch {
            ticketingRepository.approvePayment(
                PaymentApproveRequest(
                    orderId = orderId,
                    amount = totalPrice,
                    paymentKey = paymentKey,
                    showId = showId,
                    salesTicketTypeId = salesTicketTypeId,
                    ticketCount = ticketCount,
                    reservationName = reservationName,
                    reservationPhoneNumber = reservationPhoneNumber,
                    depositorName = depositorName,
                    depositorPhoneNumber = depositorPhoneNumber,
                    paymentAmount = totalPrice,
                    means = PaymentType.CARD,
                )
            ).singleOrNull()?.let { orderId ->
                event(PaymentEvent.Approved(orderId))
            }
        }
    }

    private fun event(event: PaymentEvent) {
        viewModelScope.launch {
            _event.send(event)
        }
    }
}
