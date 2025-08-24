package com.nexters.boolti.presentation.screen.profileedit.introduce

sealed interface IntroduceEditEvent {
    data object Saved : IntroduceEditEvent
}
