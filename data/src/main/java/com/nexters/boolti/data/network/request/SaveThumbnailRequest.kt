package com.nexters.boolti.data.network.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SaveThumbnailRequest(
    @SerialName("profileImagePath") val path: String,
)
