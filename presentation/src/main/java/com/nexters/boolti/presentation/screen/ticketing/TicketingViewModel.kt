package com.nexters.boolti.presentation.screen.ticketing

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.model.PaymentType
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
    private val isInviteTicket: Boolean = savedStateHandle["isInviteTicket"] ?: false
    private val userId = getUserUsecase().id

    private val _state = MutableStateFlow(TicketingState())
    val state = _state.asStateFlow()

    val userInput = TicketingUserInput()

    val paymentRequest: TicketingRequest
        get() = when (isInviteTicket) {
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
                depositorName = userInput.depositorName,
                depositorPhoneNumber = userInput.depositorPhoneNumber,
                paymentAmount = state.value.totalPrice,
                paymentType = userInput.paymentType,
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
                        )
                    }
                    userInput.paymentType = info.paymentType
                }
        }
    }

    fun toggleIsSameContactInfo() {
        _state.update {
            it.copy(isSameContactInfo = !it.isSameContactInfo)
        }
    }
}

data class TicketingUserInput(
    var reservationName: String = "",
    var reservationPhoneNumber: String = "",
    var isSameContactInfo: Boolean = false,
    var depositorName: String = "",
    var depositorPhoneNumber: String = "",
    var paymentType: PaymentType = PaymentType.AccountTransfer,
    var inviteCode: String = "",
)
