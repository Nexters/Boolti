package com.nexters.boolti.presentation.screen.search

import androidx.compose.runtime.Stable
import com.nexters.boolti.domain.model.Show
import java.time.LocalDate
import java.time.LocalDateTime

@Stable
data class SearchUiModel(
    val searchHistory: List<String>,
    val newShows: List<Show>,
    val risingKeywords: List<String>,
    val risingKeywordsTime: String,
    val showClearHistoriesDialog: Boolean,
) {
    companion object {
        val Default = SearchUiModel(
            searchHistory = emptyList(),
            newShows = emptyList(),
            risingKeywords = emptyList(),
            risingKeywordsTime = "",
            showClearHistoriesDialog = false,
        )

        val Mock = SearchUiModel(
            searchHistory = listOf("검색어1", "검색어2"),
            newShows = listOf(
                Show(
                    id = "showId1",
                    name = "A1 Show",
                    date = LocalDateTime.now(),
                    salesStartDate = LocalDate.now(),
                    salesEndDate = LocalDate.now().plusDays(1),
                    thumbnailImage = "",
                ),
                Show(
                    id = "showId2",
                    name = "A2 Show",
                    date = LocalDateTime.now(),
                    salesStartDate = LocalDate.now(),
                    salesEndDate = LocalDate.now().plusDays(1),
                    thumbnailImage = "",
                ),
                Show(
                    id = "showId3",
                    name = "A3 Show",
                    date = LocalDateTime.now(),
                    salesStartDate = LocalDate.now(),
                    salesEndDate = LocalDate.now().plusDays(1),
                    thumbnailImage = "",
                ),
            ),
            risingKeywords = listOf("키워드1", "키워드2"),
            risingKeywordsTime = "18:00",
            showClearHistoriesDialog = false,
        )
    }
}
