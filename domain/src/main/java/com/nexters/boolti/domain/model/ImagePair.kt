package com.nexters.boolti.domain.model

data class ImagePair(
    val id: String,
    val originImage: String,
    val thumbnailImage: String,
    val sequence: Int = 0,
)
