package com.nexters.boolti.presentation.screen.show

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.model.ReservationState
import com.nexters.boolti.domain.repository.ReservationRepository
import com.nexters.boolti.domain.repository.ShowRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ShowViewModel @Inject constructor(
    private val showRepository: ShowRepository,
    private val reservationRepository: ReservationRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ShowUiState())
    val uiState: StateFlow<ShowUiState> = _uiState.asStateFlow()

    init {
        search()
        fetchReservationInfo()
    }

    fun search() {
        viewModelScope.launch {
            showRepository.search(uiState.value.keyword).onSuccess { shows ->
                _uiState.update {
                    it.copy(
                        shows = shows,
                    )
                }
            }.onFailure {
                Timber.e(it)
            }
        }
    }

    fun updateKeyword(newKeyword: String) {
        _uiState.update { it.copy(keyword = newKeyword) }
    }

    private fun fetchReservationInfo() {
        reservationRepository.getReservations()
            .map { reservations ->
                reservations.firstOrNull { it.reservationState == ReservationState.DEPOSITING } != null
            }
            .onEach { hasPendingTicket ->
                _uiState.update {
                    it.copy(hasPendingTicket = hasPendingTicket)
                }
            }
            .launchIn(viewModelScope)
    }
}