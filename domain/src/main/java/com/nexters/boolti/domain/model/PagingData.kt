package com.nexters.boolti.domain.model

data class PagingData<T>(
    val items: List<T>,
    val currentPage: Int,
    val pageSize: Int,
    val totalElements: Long,
    val totalPages: Int,
    val hasNext: Boolean,
)

fun <P, Q> PagingData<Q>.map(transform: (Q) -> P): PagingData<P> =
    PagingData(
        items = items.map(transform),
        currentPage = currentPage,
        pageSize = pageSize,
        totalElements = totalElements,
        totalPages = totalPages,
        hasNext = hasNext,
    )
