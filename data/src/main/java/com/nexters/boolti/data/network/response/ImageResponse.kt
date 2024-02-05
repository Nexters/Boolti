package com.nexters.boolti.data.network.response

import com.nexters.boolti.domain.model.Image
import kotlinx.serialization.Serializable

@Serializable
data class ImageResponse(
    val id: String,
    val path: String,
    val thumbnailPath: String,
    val sequence: Int,
) {
    fun toDomain(): Image {
        return Image(
            id = id,
            path = path,
            thumbnailImage = thumbnailPath,
            sequence = sequence,
        )
    }
}

fun List<ImageResponse>.toDomains(): List<Image> = this.map { it.toDomain() }
