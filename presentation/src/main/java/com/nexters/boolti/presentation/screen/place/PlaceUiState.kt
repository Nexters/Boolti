package com.nexters.boolti.presentation.screen.place

import androidx.compose.runtime.Stable
import com.nexters.boolti.domain.model.PlaceDetail
import com.nexters.boolti.presentation.BuildConfig

@Stable
data class PlaceUiState(
    val place: PlaceDetail,
    val selectedTab: Int = 0,
    val isLoading: Boolean = true,
) {
    private val subDomain = if (BuildConfig.DEBUG) "dev.place" else "place"
    private val baseUrl = place.shareCode?.let { "https://$subDomain.boolti.in/$it" }

    // ex. "https://dev.place.boolti.in/1234/home"
    val webViewUrl = baseUrl?.let { if (selectedTab == 0) "$it/home" else "$it/rental" }
    val shareUrl = baseUrl

    companion object {
        fun getDefault(id: String) = PlaceUiState(
            place = PlaceDetail(id, "", null, null, null, null, emptyList(), null, shareCode = null),
            selectedTab = 0,
            isLoading = true,
        )
    }
}
