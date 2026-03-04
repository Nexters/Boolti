package com.nexters.boolti.presentation.screen.giftprequestion

import com.nexters.boolti.domain.model.Gift
import com.nexters.boolti.domain.model.PreQuestion
import com.nexters.boolti.presentation.extension.unicodeLength
import com.nexters.boolti.presentation.screen.ticketing.TicketingState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.persistentSetOf

sealed class GiftPreQuestionUiState {
    data object Loading : GiftPreQuestionUiState()

    data class Success(
        val gift: Gift,
        val preQuestions: ImmutableList<PreQuestion> = persistentListOf(),
        val preQuestionAnswers: ImmutableMap<Long, String> = persistentMapOf(),
    ) : GiftPreQuestionUiState()

    companion object {
        const val MAX_ANSWER_LENGTH = 100
    }

    fun getAnswerError(questionId: Long): Boolean {
        if (this !is GiftPreQuestionUiState.Success) return false

        val answer = preQuestionAnswers[questionId] ?: return false
        return answer.unicodeLength() > TicketingState.Companion.MAX_ANSWER_LENGTH
    }
}
