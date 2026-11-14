package com.nexters.boolti.data.network.response

import com.nexters.boolti.domain.model.PreQuestion
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class PreQuestionDto(
    @SerialName("id")
    val id: Long,
    @SerialName("question")
    val question: String,
    @SerialName("description")
    val description: String,
    @SerialName("isRequired")
    val isRequired: Boolean,
) {
    fun toDomain() = PreQuestion(
        id = id,
        question = question,
        description = description,
        isRequired = isRequired,
    )
}
