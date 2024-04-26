package com.nexters.boolti.presentation.screen.refund

sealed interface RefundEvent {
    data object SuccessfullyRefunded : RefundEvent
    data class ShowMessage(val message: String) : RefundEvent
}
