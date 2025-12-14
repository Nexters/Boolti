package com.nexters.boolti.presentation.screen.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface SearchRoute : NavKey {
    @Serializable
    data object RecentSearch : SearchRoute

    @Serializable
    data class SearchDetail(val keyword: String) : SearchRoute
}
