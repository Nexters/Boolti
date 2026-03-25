package com.nexters.boolti.presentation.screen.reservationdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.model.PreQuestionAnswer
import com.nexters.boolti.domain.repository.GiftRepository
import com.nexters.boolti.domain.repository.ReservationRepository
import com.nexters.boolti.domain.usecase.GetRefundPolicyUsecase
import com.nexters.boolti.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.plus
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ReservationDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val reservationRepository: ReservationRepository,
    private val giftRepository: GiftRepository,
    private val getRefundPolicyUsecase: GetRefundPolicyUsecase,
) : BaseViewModel() {
    private val reservationId: String = checkNotNull(savedStateHandle["reservationId"]) {
        "reservationId가 전달되어야 합니다."
    }

    private val isGift: Boolean = savedStateHandle["isGift"] ?: false

    private val _uiState: MutableStateFlow<ReservationDetailUiState> =
        MutableStateFlow(ReservationDetailUiState.Loading)
    val uiState: StateFlow<ReservationDetailUiState> = _uiState.asStateFlow()

    private val _refundPolicy = MutableStateFlow<List<String>>(emptyList())
    val refundPolicy = _refundPolicy.asStateFlow()

    private val _preQuestionAnswers = MutableStateFlow<ImmutableList<PreQuestionAnswer>>(persistentListOf())
    val preQuestionAnswers: StateFlow<ImmutableList<PreQuestionAnswer>> = _preQuestionAnswers.asStateFlow()

    init {
        fetchRefundPolicy()
    }

    fun fetchReservation() {
        val reservationFlow = if (isGift) {
            giftRepository.getGiftPaymentInfo(reservationId)
        } else {
            reservationRepository.findReservationById(reservationId)
        }

        reservationFlow
            .onStart {
                _uiState.update { ReservationDetailUiState.Loading }
            }
            .onEach { reservation ->
                val canShowPreQuestions = reservation.canShowPreQuestions()
                _uiState.update {
                    ReservationDetailUiState.Success(
                        reservation = reservation,
                        canShowPreQuestions = canShowPreQuestions,
                        canEditPreQuestions = reservation.canEditPreQuestions(),
                    )
                }
                if (canShowPreQuestions) {
                    fetchPreQuestionAnswers()
                } else {
                    _preQuestionAnswers.value = persistentListOf()
                }
            }
            .catch {
                _uiState.update { ReservationDetailUiState.Error() }
                throw it
            }
            .launchIn(viewModelScope + recordExceptionHandler)
    }

    fun refreshPreQuestionAnswers() {
        val state = _uiState.value as? ReservationDetailUiState.Success ?: return
        if (!state.canShowPreQuestions) {
            _preQuestionAnswers.value = persistentListOf()
            return
        }
        fetchPreQuestionAnswers()
    }

    private fun fetchPreQuestionAnswers() {
        reservationRepository.getPreQuestionAnswers(reservationId)
            .onEach { answers ->
                _preQuestionAnswers.value = answers.toImmutableList()
            }
            .catch { e ->
                Timber.e(e, "Failed to fetch pre-question answers")
            }
            .launchIn(viewModelScope + recordExceptionHandler)
    }

    private fun fetchRefundPolicy() {
        getRefundPolicyUsecase()
            .onEach { refundPolicy ->
                _refundPolicy.value = refundPolicy
            }
            .launchIn(viewModelScope + recordExceptionHandler)
    }
}
