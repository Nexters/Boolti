package com.nexters.boolti.presentation.screen.reservations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.model.ReservationState
import com.nexters.boolti.domain.repository.ReservationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ReservationsViewModel @Inject constructor(
    private val reservationRepository: ReservationRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReservationsUiState())
    val uiState: StateFlow<ReservationsUiState> = _uiState.asStateFlow()

    init {
        fetchReservations()
    }

    fun fetchReservations() {
        reservationRepository.getReservations()
            .onStart {
                // TODO 로딩 처리
            }
            .onEach { reservations ->
                _uiState.update { it.copy(reservations = reservations) }
            }
            .catch {
                it.printStackTrace()
                // TODO 예외 처리
            }
            .launchIn(viewModelScope)
    }
}