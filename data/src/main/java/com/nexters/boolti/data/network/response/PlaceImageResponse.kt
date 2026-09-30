package com.nexters.boolti.data.network.response

import com.nexters.boolti.domain.model.PlaceImage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class PlaceImageListResponse(
    @SerialName("items") val items: List<PlaceImageItemResponse> = emptyList(),
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
    @SerialName("id")
    val id: String,
    @SerialName("imageUrl")
    val imageUrl: String,
    @SerialName("thumbnailUrl")
    val thumbnailUrl: String? = null,
    @SerialName("sequence")
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
