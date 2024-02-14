package com.nexters.boolti.presentation.screen.refund

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.repository.ReservationRepository
import com.nexters.boolti.domain.request.RefundRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RefundViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val reservationRepository: ReservationRepository,
) : ViewModel() {
    private val reservationId: String = checkNotNull(savedStateHandle["reservationId"]) {
        "reservationId가 전달되어야 합니다."
    }

    private val _uiState: MutableStateFlow<RefundUiState> = MutableStateFlow(RefundUiState())
    val uiState: StateFlow<RefundUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<RefundEvent>()
    val events: SharedFlow<RefundEvent> = _events.asSharedFlow()

    init {
        fetchReservation()
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
            .catch {
                it.printStackTrace()
            }
            .launchIn(viewModelScope)
    }

    fun refund() {
        val request = RefundRequest(
            reservationId = reservationId,
            reason = uiState.value.reason,
            phoneNumber = uiState.value.contact,
            accountName = uiState.value.name,
            accountNumber = uiState.value.accountNumber,
            bankCode = uiState.value.bankInfo!!.code,
        )
        reservationRepository.refund(request).onEach {
            sendEvent(RefundEvent.SuccessfullyRefunded)
        }.catch {
            it.printStackTrace()
        }.launchIn(viewModelScope)
    }

    fun updateReason(newReason: String) {
        _uiState.update { it.copy(reason = newReason) }
    }

    fun updateName(newName: String) {
        _uiState.update { it.copy(name = newName) }
    }

    fun updateContact(newContact: String) {
        _uiState.update { it.copy(contact = newContact) }
    }

    fun updateBankInfo(newBankInfo: BankInfo) {
        _uiState.update { it.copy(bankInfo = newBankInfo) }
    }

    fun updateAccountNumber(newAccountNumber: String) {
        _uiState.update { it.copy(accountNumber = newAccountNumber) }
    }
}