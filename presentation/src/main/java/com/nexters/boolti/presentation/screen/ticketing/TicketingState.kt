package com.nexters.boolti.presentation.screen.ticketing

import com.nexters.boolti.domain.model.InviteCodeStatus
import com.nexters.boolti.domain.model.PreQuestion
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.extension.unicodeLength
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import java.time.LocalDateTime

data class TicketingState(
    val loading: Boolean = false,
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
) {
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

    fun toggleAgreement(): TicketingState = copy(orderAgreement = orderAgreement.map { it.copy(second = !orderAgreed) })

    companion object {
        const val MAX_ANSWER_LENGTH = 100
    }
}
