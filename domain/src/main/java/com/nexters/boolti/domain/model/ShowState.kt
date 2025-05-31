package com.nexters.boolti.domain.model

import java.time.Duration

sealed interface ShowState {
    data class WaitingTicketing(val remainingTime: Duration) : ShowState
    data object TicketingInProgress : ShowState
    data object ClosedTicketing : ShowState
    data object FinishedShow : ShowState

    data object NonTicketing : ShowState

    val isClosedOrFinished: Boolean
        get() = this in listOf(ClosedTicketing, FinishedShow, NonTicketing)
}
