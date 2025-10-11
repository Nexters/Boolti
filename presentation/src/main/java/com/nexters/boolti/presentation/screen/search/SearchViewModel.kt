package com.nexters.boolti.presentation.screen.search

import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.model.SearchHistory
import com.nexters.boolti.domain.repository.SearchHistoryRepository
import com.nexters.boolti.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchHistoryRepository: SearchHistoryRepository,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(SearchUiModel.Mock) // TODO Default 로 변경
    val uiState = _uiState.asStateFlow()

    init {
        observeSearchHistory()
    }

    fun onIntent(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.ClearSearchHistories -> {
                clearSearchHistories()
                setClearHistoriesDialogVisibility(false)
            }
            is SearchIntent.DeleteSearchHistory -> deleteSearchHistory(intent.keyword)
            is SearchIntent.DismissClearHistoriesDialog -> setClearHistoriesDialogVisibility(false)
            is SearchIntent.ShowClearHistoriesDialog -> setClearHistoriesDialogVisibility(true)
        }
    }

    private fun clearSearchHistories() {
        viewModelScope.launch {
            searchHistoryRepository.clearSearchHistories()
        }
    }

    private fun deleteSearchHistory(keyword: String) {
        viewModelScope.launch {
            searchHistoryRepository.deleteSearchHistory(keyword)
        }
    }

    private fun setClearHistoriesDialogVisibility(isVisible: Boolean) {
        _uiState.update {
            it.copy(showClearHistoriesDialog = isVisible)
        }
    }

    private fun observeSearchHistory() {
        viewModelScope.launch {
            searchHistoryRepository.getRecentSearchHistories(10)
                .collectLatest { searchHistories ->
                    _uiState.update {
                        it.copy(
                            searchHistory = searchHistories.map(SearchHistory::keyword),
                        )
                    }
                }
        }
    }
}
