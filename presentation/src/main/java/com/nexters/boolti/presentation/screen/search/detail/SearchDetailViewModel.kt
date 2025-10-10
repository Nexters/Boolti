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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

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

    private fun search(keyword: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _uiState.update { it.copy(loading = true) }

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
}
