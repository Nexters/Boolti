package com.nexters.boolti.presentation.screen.search.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.nexters.boolti.domain.model.Show
import com.nexters.boolti.domain.repository.SearchHistoryRepository
import com.nexters.boolti.domain.repository.SearchRepository
import com.nexters.boolti.presentation.base.BaseViewModel
import com.nexters.boolti.presentation.extension.stateInUi
import com.nexters.boolti.presentation.screen.navigation.SearchRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val searchHistoryRepository: SearchHistoryRepository,
    private val searchRepository: SearchRepository,
) : BaseViewModel() {
    private val route = savedStateHandle.toRoute<SearchRoute.SearchDetail>()
    private var searchJob: Job? = null

    private val shows = MutableStateFlow<PagingDataUiModel<Show>>(PagingDataUiModel(emptyList(), 0, 0))

    private val _uiState = MutableStateFlow(
        SearchDetailUiState.Default.copy(
            keyword = route.keyword,
        )
    )

    val uiState = combine(
        _uiState,
        shows,
    ) { uiState, shows ->
        uiState.copy(
            shows = shows.items,
            showsTotalCount = shows.totalCount,
        )
    }.stateInUi(viewModelScope, SearchDetailUiState.Default.copy(keyword = route.keyword))

    init {
        search(route.keyword)
    }

    fun onIntent(intent: SearchDetailIntent) {
        when (intent) {
            is SearchDetailIntent.ChangeTabIndex -> changeTabIndex(intent.index)
            is SearchDetailIntent.KeywordChanged -> onKeywordChanged(intent.keyword)
            is SearchDetailIntent.Search -> search(intent.keyword)
        }
    }

    private fun search(keyword: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _uiState.update { it.copy(loading = true) }

            searchHistoryRepository.saveSearchHistory(keyword)

            // 공연 검색과 프로필 검색을 동시에 실행
            val profilesDeferred = async {
                searchRepository.searchProfiles(keyword)
            }

            searchRepository.searchShows(keyword, 0).onSuccess { showsResponse ->
                shows.update {
                    PagingDataUiModel(
                        items = showsResponse.items,
                        totalCount = showsResponse.totalElements,
                        currentPage = 0,
                    )
                }
            }

            _uiState.update {
                it.copy(
                    searchedKeyword = keyword,
                    loading = false,
                )
            }
        }
    }

    private fun changeTabIndex(index: Int) {
        _uiState.update { it.copy(tabIndex = index.takeIf { i -> i < 3 } ?: 0) }
    }

    private fun onKeywordChanged(keyword: String) {
        _uiState.update { it.copy(keyword = keyword) }
    }
}
