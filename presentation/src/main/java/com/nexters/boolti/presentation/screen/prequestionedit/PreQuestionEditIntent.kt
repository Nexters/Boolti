package com.nexters.boolti.presentation.screen.prequestionedit

sealed interface PreQuestionEditIntent {
    data class SetAnswer(val questionId: Long, val answer: String) : PreQuestionEditIntent
    data object Submit : PreQuestionEditIntent
}
