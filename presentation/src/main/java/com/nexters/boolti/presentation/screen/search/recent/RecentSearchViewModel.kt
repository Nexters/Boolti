package com.nexters.boolti.presentation.screen.search.recent

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.nexters.boolti.domain.model.SearchHistory
import com.nexters.boolti.presentation.screen.navigation.SearchRoute
import com.nexters.boolti.domain.repository.SearchHistoryRepository
import com.nexters.boolti.domain.repository.SearchRepository
import com.nexters.boolti.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecentSearchViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val searchHistoryRepository: SearchHistoryRepository,
    private val searchRepository: SearchRepository,
) : BaseViewModel() {
    private val route = savedStateHandle.toRoute<SearchRoute.RecentSearch>()

    private val _uiState = MutableStateFlow(
        RecentSearchUiState.Default.copy(keyword = route.keyword)
    )
    val uiState = _uiState.asStateFlow()

    private val _event = Channel<RecentSearchEvent>()
    val event = _event.receiveAsFlow()

    init {
        observeRecentSearchKeywords()
        observeKeywordForRecommendations()
    }

    fun onIntent(intent: RecentSearchIntent) {
        when (intent) {
            is RecentSearchIntent.ClearKeyword -> changeKeyword("")
            is RecentSearchIntent.ChangeKeyword -> changeKeyword(intent.keyword)
            is RecentSearchIntent.ClearHistories -> {
                changeClearHistoriesDialogVisible(false)
                clearSearchHistories()
            }

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

    @OptIn(FlowPreview::class)
    private fun observeKeywordForRecommendations() {
        viewModelScope.launch {
            _uiState
                .map { it.searchKeyword }
                .distinctUntilChanged()
                .onEach { keyword ->
                    if (keyword.isBlank()) _uiState.update { it.copy(recommendedKeywords = emptyList()) }
                }
                .debounce(KEYWORD_INPUT_DEBOUNCE_MILLIS)
                .filter { it.isNotBlank() }
                .collectLatest { searchKeyword ->
                    searchRepository.getRecommendKeyword(searchKeyword)
                        .onSuccess { keywords ->
                            _uiState.update {
                                it.copy(recommendedKeywords = keywords)
                            }
                        }
                        .onFailure {
                            _uiState.update {
                                it.copy(recommendedKeywords = emptyList())
                            }
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

    companion object {
        private const val KEYWORD_INPUT_DEBOUNCE_MILLIS = 300L
    }
}
