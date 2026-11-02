package com.nexters.boolti.presentation.screen.search.recent

import com.nexters.boolti.presentation.screen.search.SearchSource

sealed interface RecentSearchEvent {
    data object EmptyKeyword : RecentSearchEvent
    data class Search(val keyword: String, val searchSource: SearchSource) : RecentSearchEvent
}
