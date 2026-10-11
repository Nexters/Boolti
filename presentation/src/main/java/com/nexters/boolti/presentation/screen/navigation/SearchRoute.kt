package com.nexters.boolti.presentation.screen.navigation

import com.nexters.boolti.presentation.screen.search.SearchSource
import kotlinx.serialization.Serializable

sealed interface SearchRoute {
    @Serializable
    data class RecentSearch(val keyword: String = "") : SearchRoute

    @Serializable
    data class SearchDetail(
        val keyword: String,
        val searchSource: SearchSource,
    ) : SearchRoute
}
