package com.nexters.boolti.data.network.response

import kotlinx.serialization.Serializable

@Serializable
data class UploadUrlsDto(
    val uploadUrl: String,
    val expectedUrl: String,
)
