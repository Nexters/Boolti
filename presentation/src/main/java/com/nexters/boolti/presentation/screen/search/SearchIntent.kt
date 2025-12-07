package com.nexters.boolti.presentation.screen.search

sealed interface SearchIntent {
    data class DeleteSearchHistory(val keyword: String) : SearchIntent
    data object ClearSearchHistories : SearchIntent
    data object ShowClearHistoriesDialog : SearchIntent
    data object DismissClearHistoriesDialog : SearchIntent
}
