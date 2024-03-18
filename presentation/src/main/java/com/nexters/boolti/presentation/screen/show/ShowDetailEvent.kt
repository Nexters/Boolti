package com.nexters.boolti.presentation.screen.show

sealed interface ShowDetailEvent {
    data object PopBackStack : ShowDetailEvent
    data class NavigateToImages(val index: Int) : ShowDetailEvent
}