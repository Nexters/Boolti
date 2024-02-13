package com.nexters.boolti.presentation.screen.refund

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.repository.ReservationRepository
import com.nexters.boolti.presentation.screen.reservations.ReservationDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
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

    init {
        fetchReservation()
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
}