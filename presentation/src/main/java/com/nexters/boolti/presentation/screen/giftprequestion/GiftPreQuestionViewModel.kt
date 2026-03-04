package com.nexters.boolti.presentation.screen.giftprequestion

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.model.PreQuestion
import com.nexters.boolti.domain.repository.GiftRepository
import com.nexters.boolti.domain.repository.TicketingRepository
import com.nexters.boolti.domain.request.PreQuestionAnswerRequest
import com.nexters.boolti.domain.request.SubmitPreQuestionAnswersRequest
import com.nexters.boolti.presentation.base.BaseViewModel
import com.nexters.boolti.presentation.extension.unicodeLength
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import timber.log.Timber
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

    private val _uiState = MutableStateFlow(GiftPreQuestionUiState())
    val uiState: StateFlow<GiftPreQuestionUiState> = _uiState.asStateFlow()

    private var reservationId: String? = null

    init {
        fetchGift()
        fetchPreQuestions()
    }

    private fun fetchGift() {
        viewModelScope.launch(recordExceptionHandler) {
            runCatching { giftRepository.getGift(giftUuid).first() }
                .onSuccess { gift -> reservationId = gift.reservationId }
                .onFailure { e -> Timber.e(e, "Failed to fetch gift") }
        }
    }

    private fun fetchPreQuestions() {
        ticketingRepository.getPreQuestions(showId)
            .onStart { _uiState.update { it.copy(loading = true) } }
            .onEach { questions ->
                _uiState.update {
                    it.copy(
                        loading = false,
                        preQuestions = questions.toImmutableList(),
                    )
                }
            }
            .launchIn(viewModelScope + recordExceptionHandler)
    }
}