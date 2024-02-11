package com.nexters.boolti.domain.request

import kotlinx.serialization.Serializable

@Serializable
data class QrScanRequest(
    val showId: String,
    val entryCode: String,
)
