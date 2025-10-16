package com.nexters.boolti.presentation.screen.search

import androidx.compose.runtime.Stable
import com.nexters.boolti.domain.model.Show

@Stable
data class SearchUiModel(
    val loading: Boolean,
    val searchHistory: List<String>,
    val newShows: List<Show>,
    val risingKeywords: List<String>,
    val risingKeywordsTime: String,
    val showClearHistoriesDialog: Boolean,
) {
    companion object {
        val Default = SearchUiModel(
            loading = false,
            searchHistory = emptyList(),
            newShows = emptyList(),
            risingKeywords = emptyList(),
            risingKeywordsTime = "",
            showClearHistoriesDialog = false,
        )
    }
}
