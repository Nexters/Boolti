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
    val showsTotalCount: Long,
    val showsLoading: Boolean,
    val profiles: List<User.Others>,
    val profilesTotalCount: Long,
    val profilesLoading: Boolean,
    val tabIndex: Int,
) {
    companion object {
        val Default = SearchDetailUiState(
            loading = false,
            keyword = "",
            searchedKeyword = "",
            shows = emptyList(),
            showsTotalCount = 0L,
            showsLoading = false,
            profiles = emptyList(),
            profilesTotalCount = 0L,
            profilesLoading = false,
            tabIndex = 0,
        )
    }
}
