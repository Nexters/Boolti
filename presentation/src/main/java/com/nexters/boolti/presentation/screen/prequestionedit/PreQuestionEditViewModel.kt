package com.nexters.boolti.presentation.screen.prequestionedit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.model.PreQuestionAnswer
import com.nexters.boolti.domain.repository.ReservationRepository
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
class PreQuestionEditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val reservationRepository: ReservationRepository,
) : BaseViewModel() {

    private val reservationId: String = checkNotNull(savedStateHandle["reservationId"]) {
        "reservationId가 전달되어야 합니다."
    }

    private val _uiState = MutableStateFlow(PreQuestionEditUiState())
    val uiState: StateFlow<PreQuestionEditUiState> = _uiState.asStateFlow()

    private val _events = Channel<PreQuestionEditEvent>()
    val events: Flow<PreQuestionEditEvent> = _events.receiveAsFlow()

    private var initialAnswers: ImmutableMap<Long, String>? = null

    init {
        fetchPreQuestionAnswers()
    }

    fun onIntent(intent: PreQuestionEditIntent) {
        when (intent) {
            is PreQuestionEditIntent.SetAnswer -> setAnswer(intent.questionId, intent.answer)
            is PreQuestionEditIntent.Submit -> submitAnswers()
        }
    }

    private fun sendEvent(event: PreQuestionEditEvent) {
        viewModelScope.launch {
            _events.send(event)
        }
    }

    private fun fetchPreQuestionAnswers() {
        reservationRepository.getPreQuestionAnswers(reservationId)
            .onStart {
                _uiState.update { it.copy(loading = true) }
            }
            .onEach { answerList ->
                val questions = answerList.toImmutableList()
                val answers = answerList.associate { answer ->
                    answer.preQuestionId to (answer.answer ?: "")
                }.toImmutableMap()
                initialAnswers = answers
                _uiState.update {
                    it.copy(
                        loading = false,
                        questions = questions,
                        answers = answers,
                        answerErrors = calculateAnswerErrors(answers),
                        isValid = calculateValidity(questions, answers),
                    )
                }
            }
            .catch { e ->
                Timber.e(e, "Failed to fetch pre-question answers")
                _uiState.update { it.copy(loading = false) }
                sendEvent(PreQuestionEditEvent.SaveError(e.message ?: ""))
            }
            .launchIn(viewModelScope + recordExceptionHandler)
    }

    private fun setAnswer(questionId: Long, answer: String) {
        _uiState.update {
            val newAnswers = it.answers.toMutableMap()
            newAnswers[questionId] = answer
            val immutableAnswers = newAnswers.toImmutableMap()
            it.copy(
                answers = immutableAnswers,
                answerErrors = calculateAnswerErrors(immutableAnswers),
                isValid = calculateValidity(it.questions, immutableAnswers),
            )
        }
    }

    private fun calculateAnswerErrors(answers: ImmutableMap<Long, String>) =
        answers.filterValues { it.unicodeLength() > PreQuestionEditUiState.MAX_ANSWER_LENGTH }
            .keys
            .toImmutableSet()

    private fun calculateValidity(
        questions: ImmutableList<PreQuestionAnswer>,
        answers: ImmutableMap<Long, String>,
    ): Boolean {
        val requiredAnswered = questions
            .filter { it.isRequired }
            .all { question ->
                val answer = answers[question.preQuestionId]
                !answer.isNullOrBlank() && answer.unicodeLength() <= PreQuestionEditUiState.MAX_ANSWER_LENGTH
            }

        val noInvalidAnswers = answers.values.none {
            it.unicodeLength() > PreQuestionEditUiState.MAX_ANSWER_LENGTH
        }

        return requiredAnswered && noInvalidAnswers
    }

    private fun submitAnswers() {
        val state = uiState.value
        val answerRequests = state.answers
            .filter { (_, answer) -> answer.isNotBlank() }
            .map { (questionId, answer) ->
                PreQuestionAnswerRequest(
                    preQuestionId = questionId,
                    answer = answer,
                )
            }

        val request = SubmitPreQuestionAnswersRequest(
            reservationId = reservationId,
            answers = answerRequests,
        )

        reservationRepository.updatePreQuestionAnswers(request)
            .onStart {
                _uiState.update { it.copy(loading = true) }
            }
            .onEach {
                val hasChanges = initialAnswers != state.answers
                _uiState.update { it.copy(loading = false) }
                sendEvent(PreQuestionEditEvent.SaveSuccess(hasChanges))
            }
            .catch { e ->
                Timber.e(e, "Failed to submit pre-question answers")
                _uiState.update { it.copy(loading = false) }
                sendEvent(PreQuestionEditEvent.SaveError(e.message ?: ""))
            }
            .launchIn(viewModelScope + recordExceptionHandler)
    }
}
