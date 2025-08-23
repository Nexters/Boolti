package com.nexters.boolti.presentation.screen.link

sealed interface LinkListEvent {
    data object Added : LinkListEvent
    data object Edited : LinkListEvent
    data object Removed : LinkListEvent
    data object Finish : LinkListEvent
}

sealed interface LinkEditEvent {
    data object Finish : LinkEditEvent
}
