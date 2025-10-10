package com.nexters.boolti.presentation.screen.search.recent

import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.model.SearchHistory
import com.nexters.boolti.domain.repository.SearchHistoryRepository
import com.nexters.boolti.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecentSearchViewModel @Inject constructor(
    private val searchHistoryRepository: SearchHistoryRepository,
) : BaseViewModel() {
    private val _uiState = MutableStateFlow(RecentSearchUiState.Default)
    val uiState = _uiState.asStateFlow()

    private val _event = Channel<RecentSearchEvent>()
    val event = _event.receiveAsFlow()

    init {
        observeRecentSearchKeywords()
    }

    fun onIntent(intent: RecentSearchIntent) {
        when (intent) {
            is RecentSearchIntent.ClearKeyword -> changeKeyword("")
            is RecentSearchIntent.ChangeKeyword -> changeKeyword(intent.keyword)
            is RecentSearchIntent.ClearHistories -> clearSearchHistories()
            is RecentSearchIntent.DeleteSearchHistory -> deleteSearchHistory(intent.keyword)
            is RecentSearchIntent.ShowClearHistoriesDialog -> changeClearHistoriesDialogVisible(true)
            is RecentSearchIntent.DismissClearHistoriesDialog -> changeClearHistoriesDialogVisible(false)
            is RecentSearchIntent.Search -> search(intent.keyword.trim())
        }
    }

    private fun observeRecentSearchKeywords() {
        viewModelScope.launch {
            searchHistoryRepository.getRecentSearchHistories(10)
                .collectLatest { searchHistories ->
                    _uiState.update {
                        it.copy(recentSearchKeywords = searchHistories.map(SearchHistory::keyword))
                    }
                }
        }
    }

    private fun changeKeyword(keyword: String) {
        _uiState.update {
            it.copy(keyword = keyword)
        }
    }

    private fun changeClearHistoriesDialogVisible(visible: Boolean) {
        _uiState.update {
            it.copy(showClearDialog = visible)
        }
    }

    private fun deleteSearchHistory(keyword: String) {
        viewModelScope.launch {
            searchHistoryRepository.deleteSearchHistory(keyword)
        }
    }

    private fun clearSearchHistories() {
        viewModelScope.launch {
            searchHistoryRepository.clearSearchHistories()
        }
    }

    private fun search(keyword: String) {
        if (keyword.isBlank()) {
            event(RecentSearchEvent.EmptyKeyword)
        } else {
            event(RecentSearchEvent.Search(keyword))
        }
    }

    private fun event(event: RecentSearchEvent) {
        viewModelScope.launch {
            _event.send(event)
        }
    }
}
