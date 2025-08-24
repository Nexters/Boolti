package com.nexters.boolti.presentation.screen.profileedit.usercode

sealed interface UserCodeEditEvent {
    data object Saved : UserCodeEditEvent
}
