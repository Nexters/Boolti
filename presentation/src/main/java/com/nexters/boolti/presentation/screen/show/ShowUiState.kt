package com.nexters.boolti.presentation.screen.show

import androidx.compose.runtime.Stable

@Stable
data class ShowUiState(
    val isRefreshing: Boolean = true,
    val shows: List<ShowListItem> = emptyList(),
)
