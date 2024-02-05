package com.nexters.boolti.presentation.screen.show

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.model.ShowDetail
import com.nexters.boolti.domain.model.TicketingTicket
import com.nexters.boolti.domain.repository.ShowRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ShowDetailViewModel @Inject constructor(
    private val showRepository: ShowRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ShowDetailUiState())
    val uiState: StateFlow<ShowDetailUiState> = _uiState.asStateFlow()

    init {
        fetchDummyTickets()
    }

    private fun fetchDummyTickets() {
        val tickets = buildList {
            repeat(30) {
                add(
                    TicketingTicket(
                        id = UUID.randomUUID().toString(),
                        isInviteTicket = listOf(true, false).random(),
                        title = "티켓 ${it + 1}",
                        price = (100..100000).random(),
                    )
                )
            }
        }

        val leftAmount = buildMap {
            tickets.forEach {
                put(it.id, listOf(0, 50, 100).random())
            }
        }

        _uiState.value = uiState.value.copy(tickets = tickets, leftAmount = leftAmount)
    }

    fun fetchShowDetail(id: String) {
        viewModelScope.launch {
            showRepository.searchById(id = id)
                .onSuccess { newShowDetail ->
                    _uiState.update { it.copy(showDetail = newShowDetail) }
                }
                .onFailure {
                    it.printStackTrace()
                    // todo : 예외 처리
                }
        }
    }
}