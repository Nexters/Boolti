package com.nexters.boolti.presentation.screen.place.images

sealed interface PlaceImagesEvent {
    data class NavigateToDetail(val id: String) : PlaceImagesEvent
}