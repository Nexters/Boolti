package com.nexters.boolti.domain.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ManagerCodeRequest(
    @SerialName("showId")
    val showId: String,
    @SerialName("ticketId")
    val ticketId: String,
    @SerialName("managerCode")
    val managerCode: String,
)
