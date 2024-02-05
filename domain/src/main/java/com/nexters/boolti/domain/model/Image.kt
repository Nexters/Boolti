package com.nexters.boolti.domain.model

data class Image(
    val id: String,
    val path: String,
    val thumbnailImage: String,
    val sequence: Int = 0,
)
