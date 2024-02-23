package com.nexters.boolti.presentation.screen.signout

sealed interface SignoutEvent {
    data object SignoutSuccess : SignoutEvent
}
