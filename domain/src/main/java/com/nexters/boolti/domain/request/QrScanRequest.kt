package com.nexters.boolti.domain.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QrScanRequest(
    @SerialName("showId")
    val showId: String,
    @SerialName("entryCode")
    val entryCode: String,
)
