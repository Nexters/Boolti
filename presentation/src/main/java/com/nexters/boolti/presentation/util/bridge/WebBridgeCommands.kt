package com.nexters.boolti.presentation.util.bridge

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// 웹 구현: boolti-web packages/bridge/src/commands

@WebBridgeCommand("REQUEST_TOKEN")
data object RequestToken

@Serializable
data class TokenResponse(
    @SerialName("token")
    val token: String,
)

@WebBridgeCommand("SHOW_TOAST")
data class ShowToast(
    @SerialName("message")
    val message: String,
    @SerialName("duration")
    val duration: ToastDuration = ToastDuration.SHORT,
)

enum class ToastDuration { SHORT, LONG }

@WebBridgeCommand("NAVIGATE_TO_SHOW_DETAIL")
data class NavigateToShowDetail(
    @SerialName("showId")
    val showId: Long,
)

@WebBridgeCommand("NAVIGATE_TO_PLACE_DETAIL")
data class NavigateToPlaceDetail(
    @SerialName("placeId")
    val placeId: Long,
)

@WebBridgeCommand("VIEW_PLACE_PHOTO_LIST")
data class ViewPlacePhotoList(
    @SerialName("id")
    val placeId: Long,
)

@WebBridgeCommand("VIEW_PLACE_PHOTO_DETAIL")
data class ViewPlacePhotoDetail(
    @SerialName("id")
    val placeId: Long,
    @SerialName("imageId")
    val imageId: Long,
)
