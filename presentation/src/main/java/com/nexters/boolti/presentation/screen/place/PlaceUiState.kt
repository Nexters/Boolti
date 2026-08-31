package com.nexters.boolti.presentation.screen.place

import com.nexters.boolti.domain.model.PlaceDetail

data class PlaceUiState(
    val place: PlaceDetail,
    val selectedTab: Int = 0,
    val isLoading: Boolean = true,
)
