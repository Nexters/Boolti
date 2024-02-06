package com.nexters.boolti.presentation.screen.show

import androidx.lifecycle.ViewModel
import com.nexters.boolti.domain.model.TicketingTicket
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ShowDetailViewModel @Inject constructor(

) : ViewModel() {
    private val _uiState = MutableStateFlow(ShowDetailUiState())
    val uiState: StateFlow<ShowDetailUiState> = _uiState.asStateFlow()

    init {
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

        _uiState.value = ShowDetailUiState(tickets = tickets)
    }
}