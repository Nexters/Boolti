package com.nexters.boolti.data.network.response

import com.nexters.boolti.domain.model.PlaceImage
import kotlinx.serialization.Serializable

@Serializable
internal data class PlaceImageListResponse(
    val items: List<PlaceImageItemResponse> = emptyList(),
) {
    fun toDomain(): List<PlaceImage> = items
        .map { it.toDomain() }
        .sortedBy { it.sequence }
}

/**
 * 유사 Response로 [ImageResponse]가 있으나 프로퍼티 이름이 다름 ㅠㅠ
 */
@Serializable
internal data class PlaceImageItemResponse(
    val id: String,
    val imageUrl: String,
    val thumbnailUrl: String? = null,
    val sequence: Int = 0,
) {
    fun toDomain(): PlaceImage {
        return PlaceImage(
            id = id,
            imageUrl = imageUrl,
            thumbnailUrl = thumbnailUrl ?: imageUrl,
            sequence = sequence,
        )
    }
}
