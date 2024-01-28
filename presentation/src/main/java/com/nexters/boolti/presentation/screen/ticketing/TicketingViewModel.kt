package com.nexters.boolti.presentation.screen.ticketing

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.nexters.boolti.domain.model.TicketingTicket
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class TicketingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val showId: String = requireNotNull(savedStateHandle["showId"])

    private val _state = MutableStateFlow(
        TicketingState(
            poster = "https://images.khan.co.kr/article/2023/09/12/news-p.v1.20230912.69ec17ff44f14cc28a10fff6e935e41b_P1.png",
            ticket = TicketingTicket("", false, "임영웅 콘서트", 119000),
        )
    )
    val state = _state.asStateFlow()

    fun toggleIsSameContactInfo() {
        _state.update {
            it.copy(isSameContactInfo = !it.isSameContactInfo)
        }
    }
}
