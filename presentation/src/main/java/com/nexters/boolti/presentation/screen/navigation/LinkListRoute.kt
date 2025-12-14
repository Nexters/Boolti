package com.nexters.boolti.presentation.screen.navigation

import androidx.navigation3.runtime.NavKey
import com.nexters.boolti.domain.model.UserCode
import kotlinx.serialization.Serializable

sealed interface LinkListRoute : NavKey {
    @Serializable
    data class LinkListRoot(
        val userCode: UserCode,
        val isEditMode: Boolean,
    ) : LinkListRoute

    @Serializable
    data object LinkList : LinkListRoute

    @Serializable
    data class LinkEdit(val isEditMode: Boolean) : LinkListRoute
}
