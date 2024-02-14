package com.nexters.boolti.presentation.screen.payment

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.repository.TicketingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: TicketingRepository,
) : ViewModel() {
    private val reservationId: String = requireNotNull(savedStateHandle["reservationId"]) {
        "TicketingCompleteViewModel 에 reservationId 가 전달되지 않았습니다."
    }

    private val _uiState = MutableStateFlow<PaymentState>(PaymentState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            repository.getPaymentInfo(reservationId)
                .catch { e ->
                    e.printStackTrace()
                }
                .singleOrNull()?.let {
                    _uiState.value = PaymentState.Success(reservationDetail = it)
                }
        }
    }
}
