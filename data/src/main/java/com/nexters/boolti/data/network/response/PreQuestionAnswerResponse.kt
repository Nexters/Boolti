package com.nexters.boolti.data.network.response

import com.nexters.boolti.domain.model.PreQuestionAnswer
import kotlinx.serialization.Serializable

@Serializable
internal data class PreQuestionAnswerDetailResponse(
    val reservationId: Long,
    val userId: Long,
    val answers: List<PreQuestionAnswerItemResponse>,
)

@Serializable
internal data class PreQuestionAnswerItemResponse(
    val preQuestionId: Long,
    val question: String,
    val description: String?,
    val isRequired: Boolean,
    val answer: String?,
    val createdAt: String?,
    val modifiedAt: String?,
)

internal fun PreQuestionAnswerItemResponse.toDomain() = PreQuestionAnswer(
    preQuestionId = preQuestionId,
    question = question,
    description = description.orEmpty(),
    isRequired = isRequired,
    answer = answer.orEmpty(),
)

internal fun List<PreQuestionAnswerItemResponse>.toDomains() = map { it.toDomain() }
