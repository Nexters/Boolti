package com.nexters.boolti.presentation.screen.ticket

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.repository.TicketRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TicketViewModel @Inject constructor(
    private val ticketRepository: TicketRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(TicketUiState(loading = true))
    val uiState = _uiState.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true) }
            ticketRepository.getTicket()
                .onCompletion {
                    _uiState.update { it.copy(loading = false) }
                }.catch { e ->
                    e.printStackTrace()
                }.singleOrNull()?.let { tickets ->
                    _uiState.update {
                        it.copy(tickets = tickets)
                    }
                }
        }
    }
}
