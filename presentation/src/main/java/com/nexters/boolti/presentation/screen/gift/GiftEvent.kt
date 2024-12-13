package com.nexters.boolti.presentation.screen.gift

sealed interface GiftEvent {
    data class GiftSuccess(val giftId: String) : GiftEvent
    data class ProgressPayment(val userId: String, val orderId: String) : GiftEvent
    data object NoRemainingQuantity : GiftEvent
}