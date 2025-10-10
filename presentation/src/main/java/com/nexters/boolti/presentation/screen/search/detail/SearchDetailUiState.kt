package com.nexters.boolti.presentation.screen.search.detail

import androidx.compose.runtime.Stable
import com.nexters.boolti.domain.model.Show
import com.nexters.boolti.domain.model.User

@Stable
data class SearchDetailUiState(
    val loading: Boolean,
    val keyword: String,
    val searchedKeyword: String,
    val shows: List<Show>,
    val profiles: List<User.Others>,
    val tabIndex: Int,
) {
    companion object {
        val Default = SearchDetailUiState(
            loading = false,
            keyword = "",
            searchedKeyword = "",
            shows = emptyList(),
            profiles = emptyList(),
            tabIndex = 0,
        )
    }
}
