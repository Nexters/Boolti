package com.nexters.boolti.presentation.screen.show

sealed interface ShowEvent {
    data object Search : ShowEvent
}