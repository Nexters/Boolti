package com.nexters.boolti.presentation.screen.search.detail

data class PagingDataUiModel<T>(
    val items: List<T>,
    val totalCount: Long,
    val currentPage: Int,
)
