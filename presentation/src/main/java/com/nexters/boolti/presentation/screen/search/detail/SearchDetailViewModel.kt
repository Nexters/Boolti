package com.nexters.boolti.presentation.screen.search.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.nexters.boolti.domain.repository.SearchHistoryRepository
import com.nexters.boolti.presentation.base.BaseViewModel
import com.nexters.boolti.presentation.screen.navigation.SearchRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class SearchDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val searchHistoryRepository: SearchHistoryRepository,
) : BaseViewModel() {
    private val route = savedStateHandle.toRoute<SearchRoute.SearchDetail>()

    private var searchJob: Job? = null

    private val _uiState = MutableStateFlow(
        SearchDetailUiState.Default.copy(
            keyword = route.keyword,
        )
    )
    val uiState = _uiState.asStateFlow()

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

            delay(2.seconds)
            launch(Dispatchers.IO) {
                searchHistoryRepository.saveSearchHistory(keyword)
            }

            // TODO 검색
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
