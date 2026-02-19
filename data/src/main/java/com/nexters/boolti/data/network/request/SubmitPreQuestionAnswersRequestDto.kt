package com.nexters.boolti.data.network.request

import com.nexters.boolti.domain.request.SubmitPreQuestionAnswersRequest
import kotlinx.serialization.Serializable

@Serializable
internal data class SubmitPreQuestionAnswersRequestDto(
    val reservationId: Long,
    val answers: List<PreQuestionAnswerDto>,
)

@Serializable
internal data class PreQuestionAnswerDto(
    val preQuestionId: Long,
    val answer: String,
)

internal fun SubmitPreQuestionAnswersRequest.toData() = SubmitPreQuestionAnswersRequestDto(
    reservationId = reservationId.toLong(),
    answers = answers.map { answer ->
        PreQuestionAnswerDto(
            preQuestionId = answer.preQuestionId,
            answer = answer.answer,
        )
    },
)
