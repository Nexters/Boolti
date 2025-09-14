package com.nexters.boolti.presentation.screen.navigation

import com.nexters.boolti.domain.model.UserCode
import kotlinx.serialization.Serializable

sealed interface VideoListRoute {
    @Serializable
    data class VideoListRoot(
        val userCode: UserCode,
        val isEditMode: Boolean,
    ) : VideoListRoute

    @Serializable
    data object VideoList : VideoListRoute

    @Serializable
    data class VideoEdit(val isEditMode: Boolean) : VideoListRoute
}
