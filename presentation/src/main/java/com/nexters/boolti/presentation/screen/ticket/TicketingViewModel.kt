package com.nexters.boolti.presentation.screen.ticket

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.nexters.boolti.domain.model.TicketingTicket
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TicketingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val showId: String? = savedStateHandle["showId"]
    private var selectedTicket: TicketingTicket? = null

    init {
        Timber.tag("MANGBAAM-TicketingViewModel()").d("showId: $showId")
    }

    fun selectTicket(ticket: TicketingTicket) {
        selectedTicket = ticket
    }
}
