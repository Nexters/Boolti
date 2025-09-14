package com.nexters.boolti.presentation.screen.profileedit.nickname

sealed interface NicknameEditEvent {
    data object Saved : NicknameEditEvent
}
