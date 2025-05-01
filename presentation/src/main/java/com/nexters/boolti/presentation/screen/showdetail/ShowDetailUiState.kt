package com.nexters.boolti.presentation.screen.showdetail

import androidx.compose.runtime.Stable
import com.nexters.boolti.domain.model.CastTeams
import com.nexters.boolti.domain.model.ShowDetail

@Stable
data class ShowDetailUiState(
    val showDetail: ShowDetail? = null,
    val selectedTab: Int = 0,
    val castTeams: List<CastTeams> = emptyList(),
    val shouldShowNaverMapDialog: Boolean = true,
    val isLoading: Boolean = true
) {
    val isError: Boolean = !isLoading && showDetail == null
}
