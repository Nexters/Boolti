package com.nexters.boolti.presentation.screen.prequestionedit

import com.nexters.boolti.domain.model.PreQuestionAnswer
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.persistentSetOf

data class PreQuestionEditUiState(
    val loading: Boolean = true,
    val questions: ImmutableList<PreQuestionAnswer> = persistentListOf(),
    val answers: ImmutableMap<Long, String> = persistentMapOf(),
    val answerErrors: ImmutableSet<Long> = persistentSetOf(),
    val isValid: Boolean = false,
) {
    fun isError(questionId: Long): Boolean = questionId in answerErrors

    companion object {
        const val MAX_ANSWER_LENGTH = 100
    }
}
