package com.nexters.boolti.presentation.screen.navigation

import kotlinx.serialization.Serializable

sealed interface SearchRoute {
    @Serializable
    data class RecentSearch(val keyword: String = "") : SearchRoute

    @Serializable
    data class SearchDetail(val keyword: String) : SearchRoute
}
