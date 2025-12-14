package com.nexters.boolti.presentation.screen.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface ShowRoute : NavKey {
    @Serializable
    data class ShowRoot(
        val showId: String,
        val source: String = "",
    ) : ShowRoute

    @Serializable
    data object Detail : ShowRoute

    @Serializable
    data class Images(
        val index: Int,
    ) : ShowRoute

    @Serializable
    data object Content : ShowRoute

    @Serializable
    data class Report(
        val showId: String?,
    ) : ShowRoute
}
