package com.nexters.boolti.data.network.response

import com.nexters.boolti.domain.model.PagingData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PagingResponse<T>(
    @SerialName("items")
    val items: List<T>,
    @SerialName("currentPage")
    val currentPage: Int,
    @SerialName("pageSize")
    val pageSize: Int,
    @SerialName("totalElements")
    val totalElements: Long,
    @SerialName("totalPages")
    val totalPages: Int,
    @SerialName("hasNext")
    val hasNext: Boolean,
)

fun <T> PagingResponse<T>.toPagingData(): PagingData<T> = PagingData(
    items = items,
    currentPage = currentPage,
    pageSize = pageSize,
    totalElements = totalElements,
    totalPages = totalPages,
    hasNext = hasNext,
)
