package com.nexters.boolti.domain.model

import java.time.LocalDateTime

sealed interface ShowState {
    data class WaitingTicketing(val startDateTime: LocalDateTime) : ShowState
    data object TicketingInProgress : ShowState
    data object ClosedTicketing : ShowState
    data object FinishedShow : ShowState

    val isClosedOrFinished: Boolean
        get() = this in listOf(ClosedTicketing, FinishedShow)
}