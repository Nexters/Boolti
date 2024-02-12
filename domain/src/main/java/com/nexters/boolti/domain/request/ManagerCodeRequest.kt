package com.nexters.boolti.domain.request

import kotlinx.serialization.Serializable

@Serializable
data class ManagerCodeRequest(
    val showId: String,
    val ticketId: String,
    val managerCode: String,
)
