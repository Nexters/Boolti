package com.nexters.boolti.presentation.screen.showdetail

import com.nexters.boolti.domain.model.CastTeams
import com.nexters.boolti.domain.model.ShowDetail

data class ShowDetailUiState(
    val showDetail: ShowDetail = ShowDetail(),
    val selectedTab: Int = 0,
    val castTeams: List<CastTeams> = emptyList(),
)
