package com.nexters.boolti.data.network.response

import com.nexters.boolti.domain.model.ImagePair
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class GiftImageResponse(
    @SerialName("id")
    val id: String,
    @SerialName("path")
    val path: String,
    @SerialName("preview_path") val thumbnailPath: String,
    @SerialName("sequence")
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

internal fun List<GiftImageResponse>.toDomains(): List<ImagePair> {
    return this.asSequence()
        .sortedBy { it.sequence }
        .map { it.toDomain() }
        .toList()
}
