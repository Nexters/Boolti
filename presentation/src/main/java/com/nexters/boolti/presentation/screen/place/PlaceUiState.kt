package com.nexters.boolti.presentation.screen.place

import com.nexters.boolti.domain.model.Place

// TODO: place를 nonnull로 보장하기
data class PlaceUiState(
    val place: Place,
    val selectedTab: Int = 0,
    val isLoading: Boolean = true,
)
