package com.nexters.boolti.presentation.screen.giftprequestion

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.repository.GiftRepository
import com.nexters.boolti.domain.repository.TicketingRepository
import com.nexters.boolti.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GiftPreQuestionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val giftRepository: GiftRepository, // TODO: 캐시
    private val ticketingRepository: TicketingRepository,
) : BaseViewModel() {

    private val giftUuid: String = checkNotNull(savedStateHandle["giftUuid"]) {
        "giftUuid가 전달되어야 합니다."
    }
    private val showId: String = checkNotNull(savedStateHandle["showId"]) {
        "showId가 전달되어야 합니다."
    }

    private val _uiState = MutableStateFlow<GiftPreQuestionUiState>(GiftPreQuestionUiState.Loading)
    val uiState: StateFlow<GiftPreQuestionUiState> = _uiState.asStateFlow()

    init {
        fetchGiftAndPreQuestions()
    }

    private fun fetchGiftAndPreQuestions() {
        viewModelScope.launch(recordExceptionHandler) {
            _uiState.update { GiftPreQuestionUiState.Loading }

            val gift = async {
                giftRepository.getGift(giftUuid).first()
            }
            val preQuestions = async {
                ticketingRepository.getPreQuestions(showId).first()
            }

            _uiState.update {
                GiftPreQuestionUiState.Success(
                    gift = gift.await(),
                    preQuestions = preQuestions.await().toImmutableList(),
                )
            }
        }
    }

    fun putPreQuestionAnswer(questionId: Long, answer: String) {
        _uiState.update {
            val state = it as GiftPreQuestionUiState.Success

            val newAnswers = state.preQuestionAnswers.toMutableMap()
            newAnswers[questionId] = answer
            state.copy(preQuestionAnswers = newAnswers.toImmutableMap())
        }
    }
}