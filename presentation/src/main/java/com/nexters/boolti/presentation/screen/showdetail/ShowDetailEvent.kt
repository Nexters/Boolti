package com.nexters.boolti.presentation.screen.showdetail

sealed interface ShowDetailEvent {
    data object PopBackStack : ShowDetailEvent
    data class NavigateToImages(val index: Int) : ShowDetailEvent
}