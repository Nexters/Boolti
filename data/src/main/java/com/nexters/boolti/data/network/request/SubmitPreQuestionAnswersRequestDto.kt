package com.nexters.boolti.data.network.request

import com.nexters.boolti.domain.request.SubmitPreQuestionAnswersRequest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class SubmitPreQuestionAnswersRequestDto(
    @SerialName("reservationId")
    val reservationId: Long,
    @SerialName("answers")
    val answers: List<PreQuestionAnswerDto>,
)

@Serializable
internal data class PreQuestionAnswerDto(
    @SerialName("preQuestionId")
    val preQuestionId: Long,
    @SerialName("answer")
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
