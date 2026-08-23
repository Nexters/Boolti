package com.nexters.boolti.data.network.response

import com.nexters.boolti.domain.model.PlaceImage
import kotlinx.serialization.Serializable

@Serializable
internal data class ConcertHallImageListResponse(
    val items: List<ConcertHallImageItemResponse> = emptyList(),
) {
    fun toDomain(): List<PlaceImage> = items
        .mapNotNull { it.toDomain() }
        .sortedBy { it.sequence }
}

@Serializable
internal data class ConcertHallImageItemResponse(
    val id: Long,
    val imageUrl: String? = null,
    val thumbnailUrl: String? = null,
    val sequence: Int = 0,
) {
    /**
     * 원본 URL이 없는 이미지는 크게 보기에서 사용할 수 없으므로 제외한다.
     */
    fun toDomain(): PlaceImage? {
        val imageUrl = imageUrl ?: return null
        return PlaceImage(
            id = id,
            imageUrl = imageUrl,
            thumbnailUrl = thumbnailUrl ?: imageUrl,
            sequence = sequence,
        )
    }
}
