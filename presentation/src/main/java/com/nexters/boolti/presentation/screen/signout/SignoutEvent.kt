package com.nexters.boolti.presentation.screen.signout

import androidx.annotation.StringRes

sealed interface SignoutEvent {
    data object SignoutSuccess : SignoutEvent
    data class SignoutFailed(@StringRes val message: Int) : SignoutEvent
}
