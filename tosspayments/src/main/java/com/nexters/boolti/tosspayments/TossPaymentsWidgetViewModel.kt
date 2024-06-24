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
    private val orderType: Int = savedStateHandle[EXTRA_KEY_ORDER_TYPE] ?: 0
    private val showId: String = savedStateHandle[EXTRA_KEY_SHOW_ID] ?: ""
    private val salesTicketTypeId: String = savedStateHandle[EXTRA_KEY_SALES_TICKET_ID] ?: ""
    private val ticketCount: Int = savedStateHandle[EXTRA_KEY_TICKET_COUNT] ?: 1
    private val reservationName: String = savedStateHandle[EXTRA_KEY_RESERVATION_NAME] ?: ""
    private val reservationPhoneNumber: String = savedStateHandle[EXTRA_KEY_RESERVATION_PHONE_NUMBER] ?: ""
    private val depositorName: String = savedStateHandle[EXTRA_KEY_DEPOSITOR_NAME] ?: ""
    private val depositorPhoneNumber: String = savedStateHandle[EXTRA_KEY_DEPOSITOR_PHONE_NUMBER] ?: ""

    // TODO : 별도 데이터 클래스로 정의하여 깔끔하게 초기화하기
    private val senderName: String = savedStateHandle[EXTRA_KEY_SENDER_NAME] ?: ""
    private val senderContact: String = savedStateHandle[EXTRA_KEY_SENDER_PHONE_NUMBER] ?: ""
    private val receiverName: String = savedStateHandle[EXTRA_KEY_RECEIVER_NAME] ?: ""
    private val receiverContact: String = savedStateHandle[EXTRA_KEY_RECEIVER_PHONE_NUMBER] ?: ""
    private val giftMessage: String = savedStateHandle[EXTRA_KEY_MESSAGE] ?: ""
    private val giftImageId: String = savedStateHandle[EXTRA_KEY_IMAGE_ID] ?: ""

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
            if (orderType == 0) {
                approveTicketingPayment(orderId, paymentKey, totalPrice)
            } else {
                approveGiftPayment(orderId, paymentKey, totalPrice)
            }
        }
    }

    private suspend fun approveTicketingPayment(
        orderId: String,
        paymentKey: String,
        totalPrice: Int,
    ) {
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

    private suspend fun approveGiftPayment(
        orderId: String,
        paymentKey: String,
        totalPrice: Int,
    ) {
        giftRepository.approveGiftPayment(
            GiftApproveRequest(
                orderId = orderId,
                amount = totalPrice,
                paymentKey = paymentKey,
                showId = showId,
                salesTicketTypeId = salesTicketTypeId,
                ticketCount = ticketCount,
                giftImageId = giftImageId,
                message = giftMessage,
                senderName = senderName,
                senderPhoneNumber = senderContact,
                recipientName = receiverName,
                recipientPhoneNumber = receiverContact,
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
            event(PaymentEvent.Approved(it.orderId, it.reservationId, it.giftId))
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
