package com.nexters.boolti.tosspayments

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.exception.TicketingErrorType
import com.nexters.boolti.domain.exception.TicketingException
import com.nexters.boolti.domain.repository.TicketingRepository
import com.nexters.boolti.domain.request.PaymentApproveRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.catch
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

    private var widgetLoadSuccess: Boolean = false
    private var agreementLoadSuccess: Boolean = false
    val loadSuccess: Boolean
        get() = widgetLoadSuccess && agreementLoadSuccess

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
                )
            ).catch { e ->
                if (e !is TicketingException) throw e
                if (
                    e.errorType in listOf(
                        TicketingErrorType.NoRemainingQuantity,
                        TicketingErrorType.ApprovePaymentFailed,
                        TicketingErrorType.Unknown,
                    )
                ) {
                    event(PaymentEvent.TicketSoldOut)
                }
            }.singleOrNull()?.let { (orderId, reservationId) ->
                event(PaymentEvent.Approved(orderId, reservationId))
            }
        }
    }

    fun onLoadPaymentWidget(success: Boolean) {
        widgetLoadSuccess = success
    }

    fun onLoadAgreement(success: Boolean) {
        agreementLoadSuccess = success
    }

    private fun event(event: PaymentEvent) {
        viewModelScope.launch {
            _event.send(event)
        }
    }
}
