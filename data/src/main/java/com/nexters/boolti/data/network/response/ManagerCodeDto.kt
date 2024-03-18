package com.nexters.boolti.data.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ManagerCodeDto(
    @SerialName("managerCode") val code: String,
) {
    fun toDomain(): String = code
}
