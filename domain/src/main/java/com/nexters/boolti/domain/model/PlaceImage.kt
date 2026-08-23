package com.nexters.boolti.domain.model

data class PlaceImage(
    val id: Long,
    val imageUrl: String,
    val thumbnailUrl: String,
    val sequence: Int,
)

/**
 * 웹 브릿지로 전달받은 [imageIds] 순서대로 이미지를 정렬한다.
 *
 * [imageIds]에 없는 이미지는 제외하고, [imageIds]에만 있고 실제 목록에 없는 id는 무시한다.
 * [imageIds]가 비어 있으면 원본 목록을 [PlaceImage.sequence] 순으로 반환한다.
 */
fun List<PlaceImage>.orderedBy(imageIds: List<Long>): List<PlaceImage> {
    if (imageIds.isEmpty()) return sortedBy { it.sequence }

    val imagesById = associateBy { it.id }
    return imageIds.mapNotNull { imagesById[it] }
}
