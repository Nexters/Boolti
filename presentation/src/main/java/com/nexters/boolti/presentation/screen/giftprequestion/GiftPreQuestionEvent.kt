package com.nexters.boolti.presentation.screen.giftprequestion

sealed interface GiftPreQuestionEvent {
    data object GiftRegistered : GiftPreQuestionEvent
    data object GiftRegistrationFailed : GiftPreQuestionEvent
}