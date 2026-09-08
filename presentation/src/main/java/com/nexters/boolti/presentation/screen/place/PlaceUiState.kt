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
    private val baseUrl = "https://$subDomain.boolti.in/${place.id}"
    val webViewUrl = when (selectedTab) {
        0 -> "$baseUrl/home"
        else -> "$baseUrl/rental"
    }
    val shareUrl = baseUrl

    companion object {
        fun getDefault(id: String) = PlaceUiState(
            place = PlaceDetail(id, "", null, null, null, null, emptyList(), null),
            selectedTab = 0,
            isLoading = true,
        )
    }
}
