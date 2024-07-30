package com.nexters.boolti.presentation.screen.reservations

import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.repository.ReservationRepository
import com.nexters.boolti.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.plus
import javax.inject.Inject

@HiltViewModel
class ReservationsViewModel @Inject constructor(
    private val reservationRepository: ReservationRepository,
) : BaseViewModel() {
    private val _uiState: MutableStateFlow<ReservationsUiState> =
        MutableStateFlow(ReservationsUiState.Loading)
    val uiState: StateFlow<ReservationsUiState> = _uiState.asStateFlow()

    fun fetchReservations() {
        reservationRepository.getReservations()
            .onStart {
                _uiState.update { ReservationsUiState.Loading }
            }
            .onEach { reservations ->
                if (uiState.value == reservations) return@onEach

                _uiState.update { ReservationsUiState.Success(reservations) }
            }
            .catch {
                _uiState.update { ReservationsUiState.Error }
                throw it
            }
            .launchIn(viewModelScope + recordExceptionHandler)
    }
}
