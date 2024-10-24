package com.nexters.boolti.presentation.screen.navigation

import kotlinx.serialization.Serializable

// todo : 리팩토링 예정
sealed interface ShowRoute {
    @Serializable
    data object Detail
}