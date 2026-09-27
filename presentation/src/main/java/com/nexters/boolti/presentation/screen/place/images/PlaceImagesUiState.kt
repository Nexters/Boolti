package com.nexters.boolti.presentation.screen.place.images

import androidx.compose.runtime.Stable
import com.nexters.boolti.domain.model.PlaceImage

@Stable
data class PlaceImagesUiState(
    val images: List<PlaceImage> = emptyList(),
    val isLoading: Boolean = true,
)
