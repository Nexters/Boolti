package com.nexters.boolti.data.network.response

data class ImageResponse(
    val id: String,
    val path: String,
    val thumbnailPath: String,
    val sequence: Int,
)