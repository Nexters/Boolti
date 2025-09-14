package com.nexters.boolti.data.network.response

import com.nexters.boolti.domain.model.ToggleResult
import kotlinx.serialization.Serializable

@Serializable
internal data class ToggleResultDto(
    val result: Boolean,
)

internal fun ToggleResultDto.toDomain() = ToggleResult(result)
