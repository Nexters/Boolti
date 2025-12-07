package com.nexters.boolti.presentation.screen.search.recent

sealed interface RecentSearchEvent {
    data object EmptyKeyword : RecentSearchEvent
    data class Search(val keyword: String) : RecentSearchEvent
}
