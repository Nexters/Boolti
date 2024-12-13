package com.nexters.boolti.presentation.screen.profile

sealed interface ProfileEvent {
    data object Invalid : ProfileEvent
    data object WithdrawUser : ProfileEvent
}
