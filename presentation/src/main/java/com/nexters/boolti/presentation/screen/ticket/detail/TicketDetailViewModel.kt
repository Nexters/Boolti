package com.nexters.boolti.presentation.screen.ticket.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.repository.TicketRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TicketDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: TicketRepository,
) : ViewModel() {
    private val ticketId: String = requireNotNull(savedStateHandle["ticketId"]) {
        "TicketDetailViewModel 에 ticketId 가 전달되지 않았습니다."
    }

    private val _uiState = MutableStateFlow(TicketDetailUiState())
    val uiState = _uiState.asStateFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            repository.getTicket(ticketId).catch { e ->
                e.printStackTrace()
            }.singleOrNull()?.let { ticket ->
                _uiState.update { it.copy(ticket = ticket) }
            }
        }
    }
}
