package com.nexters.boolti.presentation.screen.ticketing

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.repository.TicketingRepository
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

    private val _state = MutableStateFlow(TicketingState())
    val state = _state.asStateFlow()

    private val userInputState = MutableStateFlow(TicketingUserInput())
    val userInput
        get() = userInputState.value

    private val reservationRequest: TicketingRequest
        get() = when (state.value.isInviteTicket) {
            true -> TicketingRequest.Invite(
                inviteCode = userInput.inviteCode,
                userId = userId,
                showId = showId,
                salesTicketTypeId = salesTicketTypeId,
                reservationName = userInput.reservationName,
                reservationPhoneNumber = userInput.reservationPhoneNumber,
            )

            false -> TicketingRequest.Normal(
                ticketCount = state.value.ticketCount,
                depositorName = if (state.value.isSameContactInfo) userInput.reservationName else userInput.depositorName,
                depositorPhoneNumber = if (state.value.isSameContactInfo) userInput.reservationPhoneNumber else userInput.depositorPhoneNumber,
                paymentAmount = state.value.totalPrice,
                paymentType = state.value.paymentType,
                userId = userId,
                showId = showId,
                salesTicketTypeId = salesTicketTypeId,
                reservationName = userInput.reservationName,
                reservationPhoneNumber = userInput.reservationPhoneNumber,
            )
        }

    init {
        load()
    }

    fun reservation() {
        viewModelScope.launch {
            repository.requestReservation(reservationRequest)
                .onStart { _state.update { it.copy(loading = true) } }
                .catch { e ->
                    e.printStackTrace()
                    _state.update { it.copy(loading = false) }
                }
                .singleOrNull()?.let {
                    Timber.tag("MANGBAAM-TicketingViewModel(reservation)").d("예매 성공: $it")
                    _state.update { it.copy(loading = false) }
                }
        }
    }

    private fun load() {
        viewModelScope.launch {
            repository.getTicketingInfo(TicketingInfoRequest(showId, salesTicketTypeId, ticketCount))
                .catch { e -> e.printStackTrace() }
                .onStart {
                    _state.update { it.copy(loading = true) }
                }
                .singleOrNull()?.let { info ->
                    _state.update {
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
        _state.update {
            it.copy(isSameContactInfo = !it.isSameContactInfo)
        }
    }

    fun setReservationName(name: String) {
        userInputState.update { it.copy(reservationName = name) }
    }

    fun setReservationPhoneNumber(number: String) {
        userInputState.update { it.copy(reservationPhoneNumber = number) }
    }

    fun setDepositorName(name: String) {
        userInputState.update { it.copy(depositorName = name) }
    }

    fun setDepositorPhoneNumber(number: String) {
        userInputState.update { it.copy(depositorPhoneNumber = number) }
    }

    fun setInviteCode(code: String) {
        userInputState.update { it.copy(inviteCode = code) }
    }
}

data class TicketingUserInput(
    var reservationName: String = "",
    var reservationPhoneNumber: String = "",
    var depositorName: String = "",
    var depositorPhoneNumber: String = "",
    var inviteCode: String = "",
)
