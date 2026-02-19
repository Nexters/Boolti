package com.nexters.boolti.data.network.response

import com.nexters.boolti.domain.model.PreQuestion
import kotlinx.serialization.Serializable

@Serializable
internal data class PreQuestionDto(
    val id: Long,
    val question: String,
    val description: String,
    val isRequired: Boolean,
) {
    fun toDomain() = PreQuestion(
        id = id,
        question = question,
        description = description,
        isRequired = isRequired,
    )
}
