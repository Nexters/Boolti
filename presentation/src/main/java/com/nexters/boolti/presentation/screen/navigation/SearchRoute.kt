package com.nexters.boolti.presentation.screen.navigation

import kotlinx.serialization.Serializable

sealed interface SearchRoute {
    @Serializable
    data object RecentSearch : SearchRoute

    @Serializable
    data class SearchDetail(val keyword: String) : SearchRoute
}
