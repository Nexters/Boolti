package com.nexters.boolti.tosspayments

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.exception.TicketingErrorType
import com.nexters.boolti.domain.exception.TicketingException
import com.nexters.boolti.domain.repository.GiftRepository
import com.nexters.boolti.domain.repository.TicketingRepository
import com.nexters.boolti.domain.request.GiftApproveRequest
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
    private val giftRepository: GiftRepository,
) : ViewModel() {
    private val paymentState = TossPaymentState.from(savedStateHandle)

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
            when (paymentState) {
                is TossPaymentState.Ticketing -> approveTicketingPayment(
                    paymentState,
                    orderId,
                    paymentKey,
                    totalPrice
                )

                is TossPaymentState.Gift -> approveGiftPayment(
                    paymentState,
                    orderId,
                    paymentKey,
                    totalPrice
                )
            }
        }
    }

    private suspend fun approveTicketingPayment(
        state: TossPaymentState.Ticketing,
        orderId: String,
        paymentKey: String,
        totalPrice: Int,
    ) {
        ticketingRepository.approvePayment(
            PaymentApproveRequest(
                orderId = orderId,
                amount = totalPrice,
                paymentKey = paymentKey,
                showId = state.showId,
                salesTicketTypeId = state.salesTicketTypeId,
                ticketCount = state.ticketCount,
                reservationName = state.reservationName,
                reservationPhoneNumber = state.reservationPhoneNumber,
                depositorName = state.depositorName,
                depositorPhoneNumber = state.depositorPhoneNumber,
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

    private suspend fun approveGiftPayment(
        state: TossPaymentState.Gift,
        orderId: String,
        paymentKey: String,
        totalPrice: Int,
    ) {
        giftRepository.approveGiftPayment(
            GiftApproveRequest(
                orderId = orderId,
                amount = totalPrice,
                paymentKey = paymentKey,
                showId = state.showId,
                salesTicketTypeId = state.salesTicketTypeId,
                ticketCount = state.ticketCount,
                giftImageId = state.giftImageId,
                message = state.giftMessage,
                senderName = state.senderName,
                senderPhoneNumber = state.senderContact,
                recipientName = state.receiverName,
                recipientPhoneNumber = state.receiverContact,
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
        }.singleOrNull()?.let {
            event(PaymentEvent.Approved(it.orderId, it.reservationId, it.giftUuid))
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
