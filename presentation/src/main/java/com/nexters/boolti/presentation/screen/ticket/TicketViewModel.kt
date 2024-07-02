package com.nexters.boolti.presentation.screen.ticket

import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.repository.TicketRepository
import com.nexters.boolti.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.plus
import javax.inject.Inject

@HiltViewModel
class TicketViewModel @Inject constructor(
    private val ticketRepository: TicketRepository,
) : BaseViewModel() {
    private val _uiState = MutableStateFlow(TicketUiState(loading = true))
    val uiState = _uiState.asStateFlow()

    fun load() {
        ticketRepository.getTickets()
            .onStart { _uiState.update { it.copy(loading = true) } }
            .onCompletion { _uiState.update { it.copy(loading = false) } }
            .onEach { tickets ->
                _uiState.update {
                    it.copy(tickets = tickets)
                }
            }
            .launchIn(viewModelScope + recordExceptionHandler)
    }
}
