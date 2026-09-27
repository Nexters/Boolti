package com.nexters.boolti.presentation.screen.ticketing

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.nexters.boolti.common.tracker.AppTracker
import com.nexters.boolti.common.tracker.event.complete
import com.nexters.boolti.domain.exception.TicketingErrorType
import com.nexters.boolti.domain.exception.TicketingException
import com.nexters.boolti.domain.model.InviteCodeStatus
import com.nexters.boolti.domain.repository.TicketingRepository
import com.nexters.boolti.domain.request.CheckInviteCodeRequest
import com.nexters.boolti.domain.request.OrderIdRequest
import com.nexters.boolti.domain.request.PreQuestionAnswerRequest
import com.nexters.boolti.domain.request.SubmitPreQuestionAnswersRequest
import com.nexters.boolti.domain.request.TicketingInfoRequest
import com.nexters.boolti.domain.request.TicketingRequest
import com.nexters.boolti.domain.usecase.GetRefundPolicyUsecase
import com.nexters.boolti.domain.usecase.GetUserUsecase
import com.nexters.boolti.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TicketingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: TicketingRepository,
    getUserUsecase: GetUserUsecase,
    private val getRefundPolicyUsecase: GetRefundPolicyUsecase,
) : BaseViewModel() {
    private val showId: String = requireNotNull(savedStateHandle["showId"])
    private val salesTicketTypeId: String = requireNotNull(savedStateHandle["salesTicketId"])
    private val ticketCount: Int = savedStateHandle["ticketCount"] ?: 1
    private val userId = checkNotNull(getUserUsecase()?.id) {
        "[TicketingViewModel] 사용자 정보가 없습니다."
    }

    private val _uiState = MutableStateFlow(
        TicketingUiState(showId = showId, salesTicketTypeId = salesTicketTypeId),
    )
    val uiState: StateFlow<TicketingUiState> = _uiState.asStateFlow()

    private val _event = Channel<TicketingEvent>()
    val event: Flow<TicketingEvent> = _event.receiveAsFlow()

    private val state: TicketingUiState
        get() = uiState.value

    init {
        load()
    }

    fun onAction(action: TicketingAction) {
        when (action) {
            is TicketingAction.ChangeReservationName -> _uiState.update { it.copy(reservationName = action.name) }
            is TicketingAction.ChangeReservationContact -> _uiState.update { it.copy(reservationContact = action.contact) }
            is TicketingAction.ChangeDepositorName -> _uiState.update { it.copy(depositorName = action.name) }
            is TicketingAction.ChangeDepositorContact -> _uiState.update { it.copy(depositorContact = action.contact) }
            TicketingAction.ToggleSameContactInfo -> _uiState.update { it.copy(isSameContactInfo = !it.isSameContactInfo) }
            is TicketingAction.ChangeInviteCode -> _uiState.update {
                it.copy(inviteCode = action.code, inviteCodeStatus = InviteCodeStatus.Default)
            }
            TicketingAction.CheckInviteCode -> checkInviteCode()
            is TicketingAction.ChangePreQuestionAnswer -> _uiState.update {
                it.copy(preQuestionAnswers = (it.preQuestionAnswers + (action.questionId to action.answer)).toImmutableMap())
            }
            TicketingAction.ToggleAgreement -> _uiState.update { it.toggleAgreement() }
            is TicketingAction.ShowPolicy -> _uiState.update { it.copy(policyPageUrl = action.url) }
            TicketingAction.DismissPolicy -> _uiState.update { it.copy(policyPageUrl = null) }
            TicketingAction.ClickPayment -> _uiState.update { it.copy(dialog = TicketingDialog.Confirm) }
            TicketingAction.ConfirmReservation -> reservation()
            TicketingAction.DismissDialog -> _uiState.update { it.copy(dialog = null) }
            is TicketingAction.PaymentSucceeded -> {
                viewModelScope.launch(recordExceptionHandler) { submitPreQuestionAnswers(action.reservationId) }
                completeReservation(action.reservationId)
            }
            TicketingAction.PaymentSoldOut -> _uiState.update { it.copy(dialog = TicketingDialog.SoldOut) }
            TicketingAction.PaymentFailed -> _uiState.update { it.copy(dialog = TicketingDialog.PaymentFailure) }
        }
    }

    private fun load() {
        repository.getTicketingInfo(TicketingInfoRequest(showId, salesTicketTypeId, ticketCount))
            .onStart { _uiState.update { it.copy(loading = true) } }
            .onEach { info ->
                _uiState.update {
                    it.copy(
                        poster = info.showImg,
                        showDate = info.showDate,
                        showName = info.showName,
                        ticketName = info.saleTicketName,
                        ticketCount = info.ticketCount,
                        totalPrice = info.totalPrice,
                        isInviteTicket = info.isInviteTicket,
                    )
                }
            }
            .onCompletion { _uiState.update { it.copy(loading = false) } }
            .launchIn(viewModelScope + recordExceptionHandler)

        getRefundPolicyUsecase()
            .onEach { refundPolicy -> _uiState.update { it.copy(refundPolicy = refundPolicy) } }
            .launchIn(viewModelScope + recordExceptionHandler)

        repository.getPreQuestions(showId)
            .onEach { preQuestions -> _uiState.update { it.copy(preQuestions = preQuestions.toImmutableList()) } }
            .catch { e -> Timber.e(e, "Failed to load pre-questions") }
            .launchIn(viewModelScope + recordExceptionHandler)
    }

    private fun checkInviteCode() {
        repository.checkInviteCode(
            CheckInviteCodeRequest(
                showId = showId,
                salesTicketId = salesTicketTypeId,
                inviteCode = state.inviteCode,
            )
        )
            .onStart { _uiState.update { it.copy(loading = true) } }
            .onEach { status -> _uiState.update { it.copy(inviteCodeStatus = status) } }
            .onCompletion { _uiState.update { it.copy(loading = false) } }
            .launchIn(viewModelScope + recordExceptionHandler)
    }

    private fun reservation() {
        viewModelScope.launch(recordExceptionHandler) {
            when {
                state.isInviteTicket -> reservationInviteTicket()
                state.totalPrice > 0 -> progressPayment()
                else -> reservationFreeTicket()
            }
        }
    }

    private suspend fun progressPayment() {
        repository.requestOrderId(OrderIdRequest(showId, salesTicketTypeId, ticketCount))
            .onStart { _uiState.update { it.copy(loading = true) } }
            .onCompletion { _uiState.update { it.copy(loading = false) } }
            .firstOrNull()
            ?.let { orderId ->
                _uiState.update { it.copy(dialog = null) }
                _event.send(TicketingEvent.LaunchPayment(userId, orderId))
            }
    }

    private suspend fun reservationInviteTicket() {
        val request = TicketingRequest.Invite(
            inviteCode = state.inviteCode,
            userId = userId,
            showId = showId,
            salesTicketTypeId = salesTicketTypeId,
            reservationName = state.reservationName,
            reservationPhoneNumber = state.reservationContact,
        )
        repository.requestReservation(request)
            .onStart { _uiState.update { it.copy(loading = true) } }
            .onCompletion { _uiState.update { it.copy(loading = false) } }
            .singleOrNull()
            ?.let { reservationId ->
                submitPreQuestionAnswers(reservationId)
                completeReservation(reservationId)
            }
    }

    private suspend fun reservationFreeTicket() {
        val request = TicketingRequest.Free(
            ticketCount = ticketCount,
            userId = userId,
            showId = showId,
            salesTicketTypeId = salesTicketTypeId,
            reservationName = state.reservationName,
            reservationPhoneNumber = state.reservationContact,
        )
        repository.requestReservation(request)
            .catch { e ->
                if (e !is TicketingException) throw e
                if (
                    e.errorType in listOf(
                        TicketingErrorType.NoRemainingQuantity,
                        TicketingErrorType.ApprovePaymentFailed,
                        TicketingErrorType.Unknown,
                    )
                ) {
                    _uiState.update { it.copy(dialog = TicketingDialog.SoldOut) }
                }
            }
            .singleOrNull()
            ?.let { reservationId ->
                submitPreQuestionAnswers(reservationId)
                completeReservation(reservationId)
            }
    }

    private fun completeReservation(reservationId: String) {
        AppTracker.complete(
            target = "Purchase",
            properties = mapOf(
                "booking_type" to "Direct",
                "show_id" to showId,
                "show_name" to state.showName,
                "ticket_quantity" to state.ticketCount,
                "total_amount" to state.totalPrice,
            ),
        )
        _uiState.update { it.copy(dialog = null) }
        viewModelScope.launch { _event.send(TicketingEvent.NavigateToPaymentComplete(reservationId, showId)) }
    }

    private suspend fun submitPreQuestionAnswers(reservationId: String) {
        if (state.preQuestions.isEmpty()) return

        val request = SubmitPreQuestionAnswersRequest(
            reservationId = reservationId,
            answers = state.preQuestionAnswers.map { (questionId, answer) ->
                PreQuestionAnswerRequest(preQuestionId = questionId, answer = answer)
            },
        )
        repository.submitPreQuestionAnswers(request)
            .catch { e -> Timber.e(e, "Failed to submit pre-question answers") }
            .firstOrNull()
    }
}
