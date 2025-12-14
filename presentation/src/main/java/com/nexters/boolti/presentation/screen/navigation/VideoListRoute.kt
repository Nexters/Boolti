package com.nexters.boolti.presentation.screen.navigation

import androidx.navigation3.runtime.NavKey
import com.nexters.boolti.domain.model.UserCode
import kotlinx.serialization.Serializable

sealed interface VideoListRoute : NavKey {
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
