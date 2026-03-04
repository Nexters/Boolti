package com.nexters.boolti.presentation.screen.giftprequestion

import com.nexters.boolti.domain.model.PreQuestion
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.persistentSetOf

data class GiftPreQuestionUiState(
    val loading: Boolean = true,
    val preQuestions: ImmutableList<PreQuestion> = persistentListOf(),
) {

    companion object {
        const val MAX_ANSWER_LENGTH = 100
    }
}