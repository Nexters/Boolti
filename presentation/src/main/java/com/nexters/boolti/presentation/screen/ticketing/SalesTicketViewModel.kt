package com.nexters.boolti.presentation.screen.ticketing

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.model.TicketWithQuantity
import com.nexters.boolti.domain.repository.TicketingRepository
import com.nexters.boolti.domain.request.SalesTicketRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SalesTicketViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: TicketingRepository,
) : ViewModel() {

    private val showId: String = requireNotNull(savedStateHandle["showId"]) {
        "SalesTicketViewModel 에 showId 가 전달되지 않았습니다."
    }

    private val _uiState = MutableStateFlow(SalesTicketState())
    val uiState = _uiState.asStateFlow()

    fun load() {
        viewModelScope.launch {
            repository.getSalesTickets(SalesTicketRequest(showId))
                .firstOrNull()?.let { tickets ->
                    _uiState.update { it.copy(tickets = tickets) }
                }
        }
    }

    fun unSelectTicket() {
        _uiState.update {
            it.copy(selected = null)
        }
    }

    fun selectTicket(ticket: TicketWithQuantity) {
        _uiState.update {
            it.copy(selected = ticket)
        }
    }
}

data class SalesTicketState(
    val selected: TicketWithQuantity? = null,
    val tickets: List<TicketWithQuantity> = emptyList(),
)
