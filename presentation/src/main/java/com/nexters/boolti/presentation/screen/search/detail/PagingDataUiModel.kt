package com.nexters.boolti.presentation.screen.search.detail

data class PagingDataUiModel<T>(
    val items: List<T>,
    val totalCount: Long,
    val currentPage: Int,
    val totalPages: Int,
    val hasNext: Boolean,
) {
    companion object {
        fun <T> default(): PagingDataUiModel<T> =
            PagingDataUiModel(emptyList(), 0, 0, 0, true)
    }
}
