package com.nexters.boolti.presentation.screen.reservations

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.repository.GiftRepository
import com.nexters.boolti.domain.repository.ReservationRepository
import com.nexters.boolti.domain.usecase.GetRefundPolicyUsecase
import com.nexters.boolti.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.plus
import javax.inject.Inject

@HiltViewModel
class ReservationDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val reservationRepository: ReservationRepository,
    private val giftRepository: GiftRepository,
    private val getRefundPolicyUsecase: GetRefundPolicyUsecase,
) : BaseViewModel() {
    private val id: String = checkNotNull(savedStateHandle["reservationId"]) {
        "id가 전달되어야 합니다."
    }

    private val isGift: Boolean = savedStateHandle["isGift"] ?: false

    private val _uiState: MutableStateFlow<ReservationDetailUiState> =
        MutableStateFlow(ReservationDetailUiState.Loading)
    val uiState: StateFlow<ReservationDetailUiState> = _uiState.asStateFlow()

    private val _refundPolicy = MutableStateFlow<List<String>>(emptyList())
    val refundPolicy = _refundPolicy.asStateFlow()

    init {
        fetchRefundPolicy()
    }

    fun fetchReservation() {
        val reservationFlow = if (isGift) {
            giftRepository.getGiftPaymentInfo(id)
        } else {
            reservationRepository.findReservationById(id)
        }

        reservationFlow
            .onStart {
                _uiState.update { ReservationDetailUiState.Loading }
            }
            .onEach { reservation ->
                _uiState.update { ReservationDetailUiState.Success(reservation) }
            }
            .catch {
                _uiState.update { ReservationDetailUiState.Error() }
                throw it
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
