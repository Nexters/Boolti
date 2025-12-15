package com.nexters.boolti.presentation.screen.reservationdetail

import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.repository.GiftRepository
import com.nexters.boolti.domain.repository.ReservationRepository
import com.nexters.boolti.domain.usecase.GetRefundPolicyUsecase
import com.nexters.boolti.presentation.base.BaseViewModel
import com.nexters.boolti.presentation.screen.navigation.MainRoute
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
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

@HiltViewModel(assistedFactory = ReservationDetailViewModel.Factory::class)
class ReservationDetailViewModel @AssistedInject constructor(
    @Assisted val navKey: MainRoute.ReservationDetail,
    private val reservationRepository: ReservationRepository,
    private val giftRepository: GiftRepository,
    private val getRefundPolicyUsecase: GetRefundPolicyUsecase,
) : BaseViewModel() {
    private val reservationId: String = navKey.reservationId

    private val isGift: Boolean = navKey.isGift

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
            giftRepository.getGiftPaymentInfo(reservationId)
        } else {
            reservationRepository.findReservationById(reservationId)
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

    @AssistedFactory
    interface Factory {
        fun create(navKey: MainRoute.ReservationDetail): ReservationDetailViewModel
    }
}
