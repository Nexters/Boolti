package com.nexters.boolti.domain.model

sealed interface ShowState {
    data class WaitingTicketing(val dDay: Int) : ShowState
    data object TicketingInProgress : ShowState
    data object ClosedTicketing : ShowState
    data object FinishedShow : ShowState

    val isClosedOrFinished: Boolean
        get() = this in listOf(ClosedTicketing, FinishedShow)
}