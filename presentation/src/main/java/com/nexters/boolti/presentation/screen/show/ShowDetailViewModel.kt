package com.nexters.boolti.presentation.screen.show

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.model.ShowDetail
import com.nexters.boolti.domain.model.TicketingTicket
import com.nexters.boolti.domain.repository.AuthRepository
import com.nexters.boolti.domain.repository.ShowRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ShowDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val showRepository: ShowRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val showId: String = checkNotNull(savedStateHandle["showId"])

    private val _uiState = MutableStateFlow(ShowDetailUiState())
    val uiState: StateFlow<ShowDetailUiState> = _uiState.asStateFlow()

    val loggedIn = authRepository.loggedIn.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null,
    )

    init {
        fetchDummyTickets()
        fetchShowDetail()
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

    private fun fetchShowDetail() {
        viewModelScope.launch {
            showRepository.searchById(id = showId)
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