package com.nexters.boolti.presentation.screen.ticketing

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexters.boolti.common.tracker.AppTracker
import com.nexters.boolti.common.tracker.event.complete
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
import com.nexters.boolti.presentation.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class TicketingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: TicketingRepository,
    getUserUsecase: GetUserUsecase,
    private val getRefundPolicyUsecase: GetRefundPolicyUsecase,
) : ViewModel() {
    private val showId: String = requireNotNull(savedStateHandle["showId"])
    private val salesTicketTypeId: String = requireNotNull(savedStateHandle["salesTicketId"])
    private val ticketCount: Int = savedStateHandle["ticketCount"] ?: 1
    private val userId = checkNotNull(getUserUsecase()?.id) {
        "[TicketingViewModel] 사용자 정보가 없습니다."
    }

    private val _uiState = MutableStateFlow<TicketingUiState>(TicketingUiState.Loading)
    val uiState: StateFlow<TicketingUiState> = _uiState.asStateFlow()

    private val _event = Channel<TicketingEvent>()
    val event: Flow<TicketingEvent> = _event.receiveAsFlow()

    private var reservationJob: Job? = null
    private var inviteCodeJob: Job? = null

    init {
        load()
    }

    fun onAction(action: TicketingAction) {
        val state = uiState.value as? TicketingUiState.Success
        when (action) {
            TicketingAction.RetryLoad -> load()
            is TicketingAction.ChangeReservationName -> updateSuccess { it.copy(reservationName = action.name) }
            is TicketingAction.ChangeReservationContact -> updateSuccess { it.copy(reservationContact = action.contact) }
            is TicketingAction.ChangeDepositorName -> updateSuccess { it.copy(depositorName = action.name) }
            is TicketingAction.ChangeDepositorContact -> updateSuccess { it.copy(depositorContact = action.contact) }
            TicketingAction.ToggleSameContactInfo -> updateSuccess { it.copy(isSameContactInfo = !it.isSameContactInfo) }
            is TicketingAction.ChangeInviteCode -> updateSuccess {
                it.copy(inviteCode = action.code, inviteCodeStatus = InviteCodeStatus.Default)
            }
            TicketingAction.CheckInviteCode -> if (state != null && inviteCodeJob?.isActive != true) {
                inviteCodeJob = launchWithLoading { checkInviteCode(state) }
            }
            is TicketingAction.ChangePreQuestionAnswer -> updateSuccess {
                it.copy(preQuestionAnswers = (it.preQuestionAnswers + (action.questionId to action.answer)).toImmutableMap())
            }
            TicketingAction.ToggleAgreement -> updateSuccess { it.toggleAgreement() }
            is TicketingAction.ShowPolicy -> updateSuccess { it.copy(policyPageUrl = action.url) }
            TicketingAction.DismissPolicy -> updateSuccess { it.copy(policyPageUrl = null) }
            TicketingAction.ClickPayment -> updateSuccess { it.copy(dialog = TicketingDialog.Confirm) }
            TicketingAction.ConfirmReservation -> if (state != null && reservationJob?.isActive != true) {
                reservationJob = launchWithLoading { reservation(state) }
            }
            TicketingAction.DismissDialog -> updateSuccess { it.copy(dialog = null) }
            is TicketingAction.PaymentSucceeded -> if (state != null) {
                viewModelScope.launch { submitPreQuestionAnswers(action.reservationId, state) }
                viewModelScope.launch { completeReservation(action.reservationId, state) }
            }
            TicketingAction.PaymentSoldOut -> updateSuccess { it.copy(dialog = TicketingDialog.SoldOut) }
            TicketingAction.PaymentFailed -> updateSuccess { it.copy(dialog = TicketingDialog.PaymentFailure) }
        }
    }

    /** 사전 질문을 못 불러오면 필수 답변 없이 예매될 수 있어서, 둘 중 하나라도 실패하면 LoadFailed로 둔다. */
    private fun load() {
        viewModelScope.launch {
            _uiState.value = TicketingUiState.Loading
            val info = async { repository.getTicketingInfo(TicketingInfoRequest(showId, salesTicketTypeId, ticketCount)) }
            val preQuestions = async { repository.getPreQuestions(showId) }
            val refundPolicy = async { getRefundPolicyUsecase().catch { emit(emptyList()) }.first() }

            val ticketingInfo = info.await().onFailure { e -> Timber.e(e) }.getOrNull()
            val questions = preQuestions.await().onFailure { e -> Timber.e(e) }.getOrNull()
            _uiState.value = if (ticketingInfo == null || questions == null) {
                TicketingUiState.LoadFailed
            } else {
                TicketingUiState.Success(
                    showId = showId,
                    salesTicketTypeId = salesTicketTypeId,
                    poster = ticketingInfo.showImg,
                    showDate = ticketingInfo.showDate,
                    showName = ticketingInfo.showName,
                    ticketName = ticketingInfo.saleTicketName,
                    ticketCount = ticketingInfo.ticketCount,
                    totalPrice = ticketingInfo.totalPrice,
                    isInviteTicket = ticketingInfo.isInviteTicket,
                    refundPolicy = refundPolicy.await(),
                    preQuestions = questions.toImmutableList(),
                )
            }
        }
    }

    private suspend fun checkInviteCode(state: TicketingUiState.Success) {
        val request = CheckInviteCodeRequest(
            showId = showId,
            salesTicketId = salesTicketTypeId,
            inviteCode = state.inviteCode,
        )
        repository.checkInviteCode(request)
            .onSuccess { status -> updateSuccess { it.copy(inviteCodeStatus = status) } }
            .onFailure { e -> showError(e) }
    }

    private suspend fun reservation(state: TicketingUiState.Success) {
        when {
            state.isInviteTicket -> reserve(
                state,
                TicketingRequest.Invite(
                    inviteCode = state.inviteCode,
                    userId = userId,
                    showId = showId,
                    salesTicketTypeId = salesTicketTypeId,
                    reservationName = state.reservationName,
                    reservationPhoneNumber = state.reservationContact,
                ),
            )

            state.totalPrice > 0 -> progressPayment(state)
            else -> reserve(
                state,
                TicketingRequest.Free(
                    ticketCount = ticketCount,
                    userId = userId,
                    showId = showId,
                    salesTicketTypeId = salesTicketTypeId,
                    reservationName = state.reservationName,
                    reservationPhoneNumber = state.reservationContact,
                ),
            )
        }
    }

    private suspend fun progressPayment(state: TicketingUiState.Success) {
        repository.requestOrderId(OrderIdRequest(showId, salesTicketTypeId, ticketCount))
            .onSuccess { orderId ->
                updateSuccess { it.copy(dialog = null) }
                _event.send(TicketingEvent.LaunchPayment(userId, orderId, state))
            }
            .onFailure { e -> showError(e) }
    }

    private suspend fun reserve(state: TicketingUiState.Success, request: TicketingRequest) {
        repository.requestReservation(request)
            .onSuccess { reservationId ->
                submitPreQuestionAnswers(reservationId, state)
                completeReservation(reservationId, state)
            }
            .onFailure { e ->
                // 무료 티켓은 서버의 예매 실패 응답을 매진으로 안내한다
                if (request is TicketingRequest.Free && e is TicketingException) {
                    updateSuccess { it.copy(dialog = TicketingDialog.SoldOut) }
                } else {
                    showError(e)
                }
            }
    }

    private suspend fun completeReservation(reservationId: String, state: TicketingUiState.Success) {
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
        updateSuccess { it.copy(dialog = null) }
        _event.send(TicketingEvent.NavigateToPaymentComplete(reservationId, showId))
    }

    private suspend fun submitPreQuestionAnswers(reservationId: String, state: TicketingUiState.Success) {
        if (state.preQuestions.isEmpty()) return

        val request = SubmitPreQuestionAnswersRequest(
            reservationId = reservationId,
            answers = state.preQuestionAnswers.map { (questionId, answer) ->
                PreQuestionAnswerRequest(preQuestionId = questionId, answer = answer)
            },
        )
        repository.submitPreQuestionAnswers(request)
            .onFailure { e -> Timber.e(e, "Failed to submit pre-question answers") }
    }

    private suspend fun showError(e: Throwable) {
        Timber.e(e)
        updateSuccess { it.copy(dialog = null) }
        val message = if (e is IOException) R.string.error_network else R.string.message_unknown_error
        _event.send(TicketingEvent.ShowErrorMessage(message))
    }

    private inline fun updateSuccess(transform: (TicketingUiState.Success) -> TicketingUiState.Success) {
        _uiState.update { if (it is TicketingUiState.Success) transform(it) else it }
    }

    private fun launchWithLoading(block: suspend () -> Unit): Job = viewModelScope.launch {
        updateSuccess { it.copy(loading = true) }
        try {
            block()
        } finally {
            updateSuccess { it.copy(loading = false) }
        }
    }
}
