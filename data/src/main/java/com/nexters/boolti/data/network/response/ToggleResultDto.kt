package com.nexters.boolti.data.network.response

import com.nexters.boolti.domain.model.ToggleResult
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ToggleResultDto(
    @SerialName("result")
    val result: Boolean,
)

internal fun ToggleResultDto.toDomain() = ToggleResult(result)
