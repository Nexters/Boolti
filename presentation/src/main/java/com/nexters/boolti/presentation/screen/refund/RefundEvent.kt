package com.nexters.boolti.presentation.screen.refund

sealed interface RefundEvent {
    data object SuccessfullyRefunded : RefundEvent
}