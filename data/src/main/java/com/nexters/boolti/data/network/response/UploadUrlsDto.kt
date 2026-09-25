package com.nexters.boolti.data.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UploadUrlsDto(
    @SerialName("uploadUrl")
    val uploadUrl: String,
    @SerialName("expectedUrl")
    val expectedUrl: String,
)
