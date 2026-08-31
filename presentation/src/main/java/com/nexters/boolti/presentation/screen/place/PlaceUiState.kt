package com.nexters.boolti.presentation.screen.place

import com.nexters.boolti.domain.model.Place

data class PlaceUiState(
    val place: Place,
    val selectedTab: Int = 0,
    val isLoading: Boolean = true,
)
