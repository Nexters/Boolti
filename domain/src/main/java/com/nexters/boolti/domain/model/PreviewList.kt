package com.nexters.boolti.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PreviewList<T : Any>(
    val totalSize: Int,
    val hasMoreItems: Boolean,
    val previewItems: List<T>,
    val isVisible: Boolean? = null,
)

fun <P : Any, Q : Any> PreviewList<P>.map(transform: (P) -> Q): PreviewList<Q> =
    PreviewList(
        totalSize = totalSize,
        hasMoreItems = hasMoreItems,
        previewItems = previewItems.map(transform),
        isVisible = isVisible,
    )

inline fun <reified T : Any> emptyPreviewList() =
    PreviewList<T>(0, false, emptyList())

/**
 * # List 를 PreviewList로 변환
 *
 * 목록의 크기가 [previewSize] 보다 크면 더보기 노출.
 *
 * *서버에서 [previewSize] 개수 정책이 변경되었을 때 클라의 정책과 달라질 수 있음*
 *
 * @param previewSize 목록의 크기가 [previewSize] 보다 크면 더보기 노출
 */
fun <T : Any> List<T>.toPreviewList(
    previewSize: Int,
): PreviewList<T> = PreviewList(
    totalSize = size,
    hasMoreItems = size >= previewSize,
    previewItems = take(previewSize),
)
