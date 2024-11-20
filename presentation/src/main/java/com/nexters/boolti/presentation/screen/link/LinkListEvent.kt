package com.nexters.boolti.presentation.screen.link

sealed interface LinkListEvent {
    data object LoadFailed : LinkListEvent
}
