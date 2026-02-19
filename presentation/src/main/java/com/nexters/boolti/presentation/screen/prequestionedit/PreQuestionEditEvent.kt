package com.nexters.boolti.presentation.screen.prequestionedit

sealed interface PreQuestionEditEvent {
    data class SaveSuccess(val hasChanges: Boolean) : PreQuestionEditEvent
    data class SaveError(val message: String) : PreQuestionEditEvent
}
