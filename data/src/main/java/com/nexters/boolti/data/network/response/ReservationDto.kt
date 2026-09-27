package com.nexters.boolti.data.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ReservationDto(
    @SerialName("reservationId")
    val reservationId: String,
)
