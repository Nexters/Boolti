package com.nexters.boolti.data.network.response

import com.nexters.boolti.domain.model.PreQuestionAnswer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class PreQuestionAnswerDetailResponse(
    @SerialName("reservationId")
    val reservationId: Long,
    @SerialName("userId")
    val userId: Long,
    @SerialName("answers")
    val answers: List<PreQuestionAnswerItemResponse>,
)

@Serializable
internal data class PreQuestionAnswerItemResponse(
    @SerialName("preQuestionId")
    val preQuestionId: Long,
    @SerialName("question")
    val question: String,
    @SerialName("description")
    val description: String,
    @SerialName("isRequired")
    val isRequired: Boolean,
    @SerialName("answer")
    val answer: String,
    @SerialName("createdAt")
    val createdAt: String?,
    @SerialName("modifiedAt")
    val modifiedAt: String?,
)

internal fun PreQuestionAnswerItemResponse.toDomain() = PreQuestionAnswer(
    preQuestionId = preQuestionId,
    question = question,
    description = description,
    isRequired = isRequired,
    answer = answer,
)

internal fun List<PreQuestionAnswerItemResponse>.toDomains() = map { it.toDomain() }
