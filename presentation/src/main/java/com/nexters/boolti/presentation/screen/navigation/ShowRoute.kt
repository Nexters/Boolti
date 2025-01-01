package com.nexters.boolti.presentation.screen.navigation

import kotlinx.serialization.Serializable

sealed interface ShowRoute {
    @Serializable
    data class ShowRoot(
        val showId: String,
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
