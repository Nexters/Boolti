package com.nexters.boolti.presentation.screen.search.recent

import androidx.compose.runtime.Stable

@Stable
data class RecentSearchUiState(
    val keyword: String,
    val recommendedKeywords: List<String>,
    val recentSearchKeywords: List<String>,
    val showClearDialog: Boolean,
) {
    val showClearButton: Boolean = recentSearchKeywords.size >= 2

    companion object {
        val Default = RecentSearchUiState(
            keyword = "",
            recommendedKeywords = emptyList(),
            recentSearchKeywords = emptyList(),
            showClearDialog = false,
        )
    }
}
