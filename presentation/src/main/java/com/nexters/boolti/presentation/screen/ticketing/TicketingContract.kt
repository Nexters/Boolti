package com.nexters.boolti.presentation.screen.ticketing

import androidx.annotation.StringRes
import com.nexters.boolti.domain.model.InviteCodeStatus
import com.nexters.boolti.domain.model.PreQuestion
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.extension.unicodeLength
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import java.time.LocalDateTime

sealed interface TicketingUiState {
    data object Loading : TicketingUiState

    data object LoadFailed : TicketingUiState

    data class Success(
        val showId: String = "",
        val salesTicketTypeId: String = "",
        val poster: String = "",
        val showDate: LocalDateTime = LocalDateTime.now(),
        val showName: String = "",
        val ticketName: String = "",
        val ticketCount: Int = 1,
        val totalPrice: Int = 0,
        val isSameContactInfo: Boolean = false,
        val isInviteTicket: Boolean = false,
        val inviteCodeStatus: InviteCodeStatus = InviteCodeStatus.Default,
        val reservationName: String = "",
        val reservationContact: String = "",
        val depositorName: String = "",
        val depositorContact: String = "",
        val inviteCode: String = "",
        val refundPolicy: List<String> = emptyList(),
        val orderAgreement: List<Pair<Int, Boolean>> = listOf(
            Pair(R.string.order_agreement_privacy_collection, false),
            Pair(R.string.order_agreement_privacy_offer, false),
        ),
        val preQuestions: ImmutableList<PreQuestion> = persistentListOf(),
        val preQuestionAnswers: ImmutableMap<Long, String> = persistentMapOf(),
        val dialog: TicketingDialog? = null,
        val policyPageUrl: String? = null,
    ) : TicketingUiState {
        val orderAgreed: Boolean
            get() = orderAgreement.none { !it.second }

        private val isBasicInfoValid: Boolean
            get() = orderAgreed &&
                    reservationName.isNotBlank() &&
                    reservationContact.isNotBlank()

        private val isRequiredQuestionsAnswered: Boolean
            get() = preQuestions
                .filter { it.isRequired }
                .all { question ->
                    val answer = preQuestionAnswers[question.id]
                    !answer.isNullOrBlank() && answer.unicodeLength() <= MAX_ANSWER_LENGTH
                }

        private val hasInvalidAnswers: Boolean
            get() = preQuestionAnswers.values.any { it.unicodeLength() > MAX_ANSWER_LENGTH }

        private val isPreQuestionsValid: Boolean
            get() = isRequiredQuestionsAnswered && !hasInvalidAnswers

        private val isPaymentInfoValid: Boolean
            get() = when {
                isInviteTicket -> inviteCodeStatus is InviteCodeStatus.Valid
                totalPrice == 0 -> true
                else -> isSameContactInfo ||
                        (depositorName.isNotBlank() && depositorContact.isNotBlank())
            }

        val reservationButtonEnabled: Boolean
            get() = isBasicInfoValid && isPreQuestionsValid && isPaymentInfoValid

        fun getAnswerError(questionId: Long): Boolean {
            val answer = preQuestionAnswers[questionId] ?: return false
            return answer.unicodeLength() > MAX_ANSWER_LENGTH
        }

        val depositor: String
            get() = if (isSameContactInfo) reservationName else depositorName

        val depositorPhoneNumber: String
            get() = if (isSameContactInfo) reservationContact else depositorContact

        fun toggleAgreement(): Success = copy(orderAgreement = orderAgreement.map { it.copy(second = !orderAgreed) })
    }

    companion object {
        const val MAX_ANSWER_LENGTH = 100
    }
}

enum class TicketingDialog { Confirm, PaymentFailure, SoldOut }

sealed interface TicketingAction {
    data object RetryLoad : TicketingAction
    data class ChangeReservationName(val name: String) : TicketingAction
    data class ChangeReservationContact(val contact: String) : TicketingAction
    data class ChangeDepositorName(val name: String) : TicketingAction
    data class ChangeDepositorContact(val contact: String) : TicketingAction
    data object ToggleSameContactInfo : TicketingAction
    data class ChangeInviteCode(val code: String) : TicketingAction
    data object CheckInviteCode : TicketingAction
    data class ChangePreQuestionAnswer(val questionId: Long, val answer: String) : TicketingAction
    data object ToggleAgreement : TicketingAction
    data class ShowPolicy(val url: String) : TicketingAction
    data object DismissPolicy : TicketingAction
    data object ClickPayment : TicketingAction
    data object ConfirmReservation : TicketingAction
    data object DismissDialog : TicketingAction
    data class PaymentSucceeded(val reservationId: String) : TicketingAction
    data object PaymentSoldOut : TicketingAction
    data object PaymentFailed : TicketingAction
}

sealed interface TicketingEvent {
    data class NavigateToPaymentComplete(val reservationId: String, val showId: String) : TicketingEvent
    data class LaunchPayment(
        val userId: String,
        val orderId: String,
        val ticketing: TicketingUiState.Success,
    ) : TicketingEvent
    data class ShowErrorMessage(@StringRes val messageRes: Int) : TicketingEvent
}
