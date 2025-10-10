package com.nexters.boolti.presentation.screen.search.recent

sealed interface RecentSearchIntent {
    // 검색어
    data object ClearKeyword : RecentSearchIntent
    data class ChangeKeyword(val keyword: String) : RecentSearchIntent

    // 최근 검색어
    data object ClearHistories : RecentSearchIntent
    data class DeleteSearchHistory(val keyword: String) : RecentSearchIntent
    data object ShowClearHistoriesDialog : RecentSearchIntent
    data object DismissClearHistoriesDialog : RecentSearchIntent

    // 검색
    data class Search(val keyword: String) : RecentSearchIntent
}
