package com.nexters.boolti.presentation.screen.place.images

import com.nexters.boolti.domain.model.PlaceImage

data class PlaceImagesUiState(
    val images: List<PlaceImage> = emptyList(),
    val isLoading: Boolean = true,
)
