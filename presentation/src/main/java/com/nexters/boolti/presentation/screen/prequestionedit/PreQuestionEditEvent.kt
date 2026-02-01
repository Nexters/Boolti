package com.nexters.boolti.presentation.screen.prequestionedit

sealed interface PreQuestionEditEvent {
    data object SaveSuccess : PreQuestionEditEvent
    data class SaveError(val message: String) : PreQuestionEditEvent
}
