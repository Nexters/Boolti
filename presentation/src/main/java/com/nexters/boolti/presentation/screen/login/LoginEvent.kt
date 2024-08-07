package com.nexters.boolti.presentation.screen.login

sealed interface LoginEvent {
    data object Success : LoginEvent
    data object RequireSignUp : LoginEvent
    data object SignOutCancelled : LoginEvent
    data object Invalid : LoginEvent
    data object SignupFailed : LoginEvent
}
