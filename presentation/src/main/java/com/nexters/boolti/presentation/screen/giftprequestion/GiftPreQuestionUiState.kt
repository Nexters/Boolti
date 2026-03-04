package com.nexters.boolti.presentation.screen.giftprequestion

import com.nexters.boolti.domain.model.Gift
import com.nexters.boolti.domain.model.PreQuestion
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.persistentSetOf

sealed interface GiftPreQuestionUiState {
    data object Loading : GiftPreQuestionUiState

    data class Success(
        val gift: Gift,
        val preQuestions: ImmutableList<PreQuestion> = persistentListOf(),
    ) : GiftPreQuestionUiState

    companion object {
        const val MAX_ANSWER_LENGTH = 100
    }
}
