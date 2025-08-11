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
        previewItems = previewItems.map(transform)
    )

inline fun <reified T : Any> emptyPreviewList() =
    PreviewList<T>(0, false, emptyList())
