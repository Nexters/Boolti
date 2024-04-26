package com.nexters.boolti.presentation.screen.payment

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.repository.TicketingRepository
import com.nexters.boolti.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentCompleteViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: TicketingRepository,
) : BaseViewModel() {
    private val reservationId: String = requireNotNull(savedStateHandle["reservationId"]) {
        "TicketingCompleteViewModel 에 reservationId 가 전달되지 않았습니다."
    }

    private val _uiState = MutableStateFlow(PaymentCompleteState())
    val uiState = _uiState.asStateFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch(recordExceptionHandler) {
            repository.getPaymentInfo(reservationId)
                .onStart { _uiState.update { it.copy(loading = false) } }
                .catch { e -> throw e }
                .onCompletion { _uiState.update { it.copy(loading = false) } }
                .singleOrNull()?.let { reservationDetail ->
                    _uiState.update { it.copy(reservationDetail = reservationDetail) }
                }
        }
    }
}
