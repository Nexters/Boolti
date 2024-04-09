package com.nexters.boolti.data.network.response

import com.nexters.boolti.domain.model.ImagePair
import kotlinx.serialization.Serializable

@Serializable
internal data class ImageResponse(
    val id: String,
    val path: String,
    val thumbnailPath: String,
    val sequence: Int,
) {
    fun toDomain(): ImagePair {
        return ImagePair(
            id = id,
            originImage = path,
            thumbnailImage = thumbnailPath,
        )
    }
}

internal fun List<ImageResponse>.toDomains(): List<ImagePair> {
    return this.asSequence()
        .sortedBy { it.sequence }
        .map { it.toDomain() }
        .toList()
}
