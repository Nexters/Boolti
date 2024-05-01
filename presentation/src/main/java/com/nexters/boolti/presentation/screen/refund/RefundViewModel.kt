package com.nexters.boolti.presentation.screen.refund

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.repository.ReservationRepository
import com.nexters.boolti.domain.repository.TicketingRepository
import com.nexters.boolti.domain.request.PaymentCancelRequest
import com.nexters.boolti.domain.request.RefundRequest
import com.nexters.boolti.domain.usecase.GetRefundPolicyUsecase
import com.nexters.boolti.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

@HiltViewModel
class RefundViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val reservationRepository: ReservationRepository,
    private val ticketingRepository: TicketingRepository,
    private val getRefundPolicyUsecase: GetRefundPolicyUsecase,
) : BaseViewModel() {
    private val reservationId: String = checkNotNull(savedStateHandle["reservationId"]) {
        "reservationId가 전달되어야 합니다."
    }

    private val _uiState: MutableStateFlow<RefundUiState> = MutableStateFlow(RefundUiState())
    val uiState: StateFlow<RefundUiState> = _uiState.asStateFlow()

    private val _refundPolicy = MutableStateFlow<List<String>>(emptyList())
    val refundPolicy = _refundPolicy.asStateFlow()

    private val _events = MutableSharedFlow<RefundEvent>()
    val events: SharedFlow<RefundEvent> = _events.asSharedFlow()

    init {
        fetchReservation()
        fetchRefundPolicy()
    }

    private fun sendEvent(event: RefundEvent) {
        viewModelScope.launch {
            _events.emit(event)
        }
    }

    private fun fetchReservation() {
        reservationRepository.findReservationById(reservationId)
            .onEach { reservation ->
                _uiState.update { it.copy(reservation = reservation) }
            }
            .launchIn(viewModelScope + recordExceptionHandler)
    }

    fun refund() {
        val request = PaymentCancelRequest(
            reservationId = reservationId,
            cancelReason = uiState.value.reason
        )

        ticketingRepository.cancelPayment(request).onEach { isSuccessful ->
            if (isSuccessful) sendEvent(RefundEvent.SuccessfullyRefunded)
            else sendEvent(RefundEvent.RefundError)
        }.launchIn(viewModelScope + recordExceptionHandler)
    }

    fun updateReason(newReason: String) {
        _uiState.update { it.copy(reason = newReason) }
    }

    fun toggleRefundPolicyCheck(selected: Boolean) {
        _uiState.update { it.copy(refundPolicyChecked = selected) }
    }

    private fun fetchRefundPolicy() {
        getRefundPolicyUsecase()
            .onEach { refundPolicy ->
                _refundPolicy.value = refundPolicy
            }
            .launchIn(viewModelScope + recordExceptionHandler)
    }
}
