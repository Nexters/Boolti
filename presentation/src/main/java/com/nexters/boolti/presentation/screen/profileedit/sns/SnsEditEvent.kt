package com.nexters.boolti.presentation.screen.profileedit.sns

sealed interface SnsEditEvent {
    data object Saved : SnsEditEvent
}
