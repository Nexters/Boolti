package com.nexters.boolti.presentation.screen.ticketing

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.model.InviteCodeStatus
import com.nexters.boolti.domain.repository.TicketingRepository
import com.nexters.boolti.domain.request.CheckInviteCodeRequest
import com.nexters.boolti.domain.request.OrderIdRequest
import com.nexters.boolti.domain.request.PaymentApproveRequest
import com.nexters.boolti.domain.request.TicketingInfoRequest
import com.nexters.boolti.domain.request.TicketingRequest
import com.nexters.boolti.domain.usecase.GetRefundPolicyUsecase
import com.nexters.boolti.domain.usecase.GetUserUsecase
import com.nexters.boolti.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TicketingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: TicketingRepository,
    getUserUsecase: GetUserUsecase,
    private val getRefundPolicyUsecase: GetRefundPolicyUsecase,
) : BaseViewModel() {
    val showId: String = requireNotNull(savedStateHandle["showId"])
    val salesTicketTypeId: String = requireNotNull(savedStateHandle["salesTicketId"])
    private val ticketCount: Int = savedStateHandle["ticketCount"] ?: 1
    private val userId = getUserUsecase().id

    private val _uiState = MutableStateFlow(TicketingState())
    val uiState = _uiState.asStateFlow()

    private val _event = Channel<TicketingEvent>()
    val event = _event.receiveAsFlow()

    private val state: TicketingState
        get() = uiState.value

    init {
        load()
    }

    fun reservation() {
        viewModelScope.launch(recordExceptionHandler) {
            when {
                state.isInviteTicket -> reservationInviteTicket()
                !state.isInviteTicket && state.totalPrice > 0 -> reservationSalesTicket()
                else -> reservationFreeTicket()
            }
        }
    }

    private suspend fun reservationSalesTicket() {
        getOrderId()
            .onStart { _uiState.update { it.copy(loading = true) } }
            .onEach { orderId ->
                Timber.tag("[MANGBAAM]TicketingViewModel").d("reservation orderId: %s", orderId)
                event(TicketingEvent.ProgressPayment(userId, orderId))
            }
            .onCompletion { _uiState.update { it.copy(loading = false) } }
            .firstOrNull()
    }

    private suspend fun reservationInviteTicket() {
        val request = TicketingRequest.Invite(
            inviteCode = state.inviteCode,
            userId = userId,
            showId = showId,
            salesTicketTypeId = salesTicketTypeId,
            reservationName = state.reservationName,
            reservationPhoneNumber = state.reservationContact,
        )
        repository.requestReservation(request)
            .onStart { _uiState.update { it.copy(loading = true) } }
            .catch { e ->
                _uiState.update { it.copy(loading = false) }
                throw e
            }
            .onCompletion { _uiState.update { it.copy(loading = false) } }
            .singleOrNull()?.let { reservationId ->
                Timber.tag("MANGBAAM-TicketingViewModel(reservation)").d("예매 성공: $reservationId")
                event(TicketingEvent.TicketingSuccess(reservationId, showId))
            }
    }

    private suspend fun reservationFreeTicket() {
        val request = PaymentApproveRequest(
            orderId = getOrderId().firstOrNull() ?: return,
            amount = uiState.value.totalPrice,
            paymentKey = "",
            showId = showId,
            salesTicketTypeId = salesTicketTypeId,
            ticketCount = ticketCount,
            reservationName = uiState.value.reservationName,
            reservationPhoneNumber = uiState.value.reservationContact,
            depositorName = uiState.value.depositorName,
            depositorPhoneNumber = uiState.value.depositorContact,
            paymentAmount = uiState.value.totalPrice,
            means = com.nexters.boolti.domain.model.PaymentType.CARD,
        )
        repository.approvePayment(request) // TODO 무료 티켓 API 확인
            .singleOrNull()?.let {
                event(TicketingEvent.TicketingSuccess(it.reservationId, showId))
            }
    }

    private fun load() {
        viewModelScope.launch(recordExceptionHandler) {
            repository.getTicketingInfo(TicketingInfoRequest(showId, salesTicketTypeId, ticketCount))
                .onStart {
                    _uiState.update { it.copy(loading = true) }
                }
                .singleOrNull()?.let { info ->
                    _uiState.update {
                        it.copy(
                            loading = false,
                            poster = info.showImg,
                            showDate = info.showDate,
                            showName = info.showName,
                            ticketName = info.saleTicketName,
                            ticketCount = info.ticketCount,
                            totalPrice = info.totalPrice,
                            isInviteTicket = info.isInviteTicket,
                        )
                    }
                }
            getRefundPolicyUsecase()
                .onEach { refundPolicy ->
                    _uiState.update {
                        it.copy(refundPolicy = refundPolicy)
                    }
                }
                .launchIn(viewModelScope + recordExceptionHandler)
        }
    }

    fun toggleIsSameContactInfo() {
        _uiState.update {
            it.copy(isSameContactInfo = !it.isSameContactInfo)
        }
    }

    fun checkInviteCode() {
        viewModelScope.launch(recordExceptionHandler) {
            repository.checkInviteCode(
                CheckInviteCodeRequest(
                    showId = showId,
                    salesTicketId = salesTicketTypeId,
                    inviteCode = state.inviteCode,
                )
            ).onStart {
                _uiState.update { it.copy(loading = true) }
            }.catch { e ->
                _uiState.update { it.copy(loading = false) }
                throw e
            }.singleOrNull()?.let { status ->
                _uiState.update {
                    it.copy(loading = false, inviteCodeStatus = status)
                }
            }
        }
    }

    fun setReservationName(name: String) {
        _uiState.update { it.copy(reservationName = name) }
    }

    fun setReservationPhoneNumber(number: String) {
        _uiState.update { it.copy(reservationContact = number) }
    }

    fun setDepositorName(name: String) {
        _uiState.update { it.copy(depositorName = name) }
    }

    fun setDepositorPhoneNumber(number: String) {
        _uiState.update { it.copy(depositorContact = number) }
    }

    fun setInviteCode(code: String) {
        _uiState.update { it.copy(inviteCode = code, inviteCodeStatus = InviteCodeStatus.Default) }
    }

    fun toggleAgreement(index: Int) {
        _uiState.update { it.toggleAgreement(index) }
    }

    fun toggleAgreement() {
        _uiState.update { it.toggleAgreement() }
    }

    private fun getOrderId(): Flow<String> {
        return repository.getOrderId(OrderIdRequest(showId, salesTicketTypeId, ticketCount))
    }

    private fun event(event: TicketingEvent) {
        viewModelScope.launch {
            _event.send(event)
        }
    }
}
