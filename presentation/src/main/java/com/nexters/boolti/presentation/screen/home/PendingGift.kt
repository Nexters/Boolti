package com.nexters.boolti.presentation.screen.home

sealed interface PendingGift {
    data class Unprocessed(val giftUuid: String) : PendingGift

    data class Ready(
        val giftUuid: String,
        val showId: String,
    ) : PendingGift
}