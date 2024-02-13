package com.nexters.boolti.presentation.screen.ticketing

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.model.InviteCodeStatus
import com.nexters.boolti.domain.repository.TicketingRepository
import com.nexters.boolti.domain.request.CheckInviteCodeRequest
import com.nexters.boolti.domain.request.TicketingInfoRequest
import com.nexters.boolti.domain.request.TicketingRequest
import com.nexters.boolti.domain.usecase.GetUserUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TicketingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: TicketingRepository,
    getUserUsecase: GetUserUsecase,
) : ViewModel() {
    private val showId: String = requireNotNull(savedStateHandle["showId"])
    private val salesTicketTypeId: String = requireNotNull(savedStateHandle["salesTicketId"])
    private val ticketCount: Int = savedStateHandle["ticketCount"] ?: 1
    private val userId = getUserUsecase().id

    private val _uiState = MutableStateFlow(TicketingState())
    val uiState = _uiState.asStateFlow()

    private val state: TicketingState
        get() = uiState.value

    private val reservationRequest: TicketingRequest
        get() = when (uiState.value.isInviteTicket) {
            true -> TicketingRequest.Invite(
                inviteCode = state.inviteCode,
                userId = userId,
                showId = showId,
                salesTicketTypeId = salesTicketTypeId,
                reservationName = state.reservationName,
                reservationPhoneNumber = state.reservationPhoneNumber,
            )

            false -> TicketingRequest.Normal(
                ticketCount = uiState.value.ticketCount,
                depositorName = if (uiState.value.isSameContactInfo) state.reservationName else state.depositorName,
                depositorPhoneNumber = if (uiState.value.isSameContactInfo) state.reservationPhoneNumber else state.depositorPhoneNumber,
                paymentAmount = uiState.value.totalPrice,
                paymentType = uiState.value.paymentType,
                userId = userId,
                showId = showId,
                salesTicketTypeId = salesTicketTypeId,
                reservationName = state.reservationName,
                reservationPhoneNumber = state.reservationPhoneNumber,
            )
        }

    init {
        load()
    }

    fun reservation() {
        viewModelScope.launch {
            repository.requestReservation(reservationRequest)
                .onStart { _uiState.update { it.copy(loading = true) } }
                .catch { e ->
                    e.printStackTrace()
                    _uiState.update { it.copy(loading = false) }
                }
                .singleOrNull()?.let {
                    Timber.tag("MANGBAAM-TicketingViewModel(reservation)").d("예매 성공: $it")
                    _uiState.update { it.copy(loading = false) }
                }
        }
    }

    private fun load() {
        viewModelScope.launch {
            repository.getTicketingInfo(TicketingInfoRequest(showId, salesTicketTypeId, ticketCount))
                .catch { e -> e.printStackTrace() }
                .onStart {
                    _uiState.update { it.copy(loading = true) }
                }
                .singleOrNull()?.let { info ->
                    _uiState.update {
                        it.copy(
                            loading = false,
                            poster = info.showImg,
                            showDate = info.showDate,
                            showName = info.showName,
                            ticketName = info.saleTicketName,
                            ticketCount = info.ticketCount,
                            totalPrice = info.totalPrice,
                            isInviteTicket = info.isInviteTicket,
                            paymentType = info.paymentType,
                        )
                    }
                }
        }
    }

    fun toggleIsSameContactInfo() {
        _uiState.update {
            it.copy(isSameContactInfo = !it.isSameContactInfo)
        }
    }

    fun checkInviteCode() {
        viewModelScope.launch {
            repository.checkInviteCode(
                CheckInviteCodeRequest(
                    showId = showId,
                    salesTicketId = salesTicketTypeId,
                    inviteCode = state.inviteCode,
                )
            ).onStart {
                _uiState.update { it.copy(loading = true) }
            }.catch { e ->
                e.printStackTrace()
                _uiState.update { it.copy(loading = false) }
            }.singleOrNull()?.let { status ->
                _uiState.update {
                    it.copy(loading = false, inviteCodeStatus = status)
                }
            }
        }
    }

    fun setReservationName(name: String) {
        _uiState.update { it.copy(reservationName = name) }
    }

    fun setReservationPhoneNumber(number: String) {
        _uiState.update { it.copy(reservationPhoneNumber = number) }
    }

    fun setDepositorName(name: String) {
        _uiState.update { it.copy(depositorName = name) }
    }

    fun setDepositorPhoneNumber(number: String) {
        _uiState.update { it.copy(depositorPhoneNumber = number) }
    }

    fun setInviteCode(code: String) {
        _uiState.update { it.copy(inviteCode = code, inviteCodeStatus = InviteCodeStatus.Default) }
    }
}
