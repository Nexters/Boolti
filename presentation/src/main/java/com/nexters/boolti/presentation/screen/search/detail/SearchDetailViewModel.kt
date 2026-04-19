package com.nexters.boolti.presentation.screen.search.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.nexters.boolti.domain.model.Show
import com.nexters.boolti.domain.model.User
import com.nexters.boolti.domain.model.Venue
import com.nexters.boolti.domain.repository.SearchHistoryRepository
import com.nexters.boolti.domain.repository.SearchRepository
import com.nexters.boolti.presentation.base.BaseViewModel
import com.nexters.boolti.presentation.extension.stateInUi
import com.nexters.boolti.presentation.screen.navigation.SearchRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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
    private var searchShowsJob: Job? = null
    private var searchProfilesJob: Job? = null
    private var searchVenuesJob: Job? = null

    private val shows = MutableStateFlow<PagingDataUiModel<Show>>(PagingDataUiModel.default())
    private val profiles = MutableStateFlow<PagingDataUiModel<User.Others>>(PagingDataUiModel.default())
    private val venues = MutableStateFlow<PagingDataUiModel<Venue>>(PagingDataUiModel.default())

    private val _uiState = MutableStateFlow(
        SearchDetailUiState.Default.copy(
            keyword = route.keyword,
        )
    )

    val uiState = combine(
        _uiState,
        shows,
        profiles,
        venues,
    ) { uiState, shows, profiles, venues ->
        uiState.copy(
            shows = shows.items,
            showsTotalCount = shows.totalCount,
            profiles = profiles.items,
            profilesTotalCount = profiles.totalCount,
            venues = venues.items,
            venuesTotalCount = venues.totalCount,
        )
    }.stateInUi(viewModelScope, SearchDetailUiState.Default.copy(keyword = route.keyword))

    init {
        search(route.keyword)
    }

    fun onIntent(intent: SearchDetailIntent) {
        when (intent) {
            is SearchDetailIntent.ChangeTabIndex -> changeTabIndex(intent.index)
            is SearchDetailIntent.OnProfilesPageReached -> loadNextProfilesPage()
            is SearchDetailIntent.OnShowsPageReached -> loadNextShowsPage()
            is SearchDetailIntent.OnVenuesPageReached -> loadNextVenuesPage()
        }
    }

    private fun search(keyword: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _uiState.update { it.copy(loading = true) }

            searchHistoryRepository.saveSearchHistory(keyword)

            listOf(
                async {
                    searchRepository.searchShows(keyword, 0).onSuccess { response ->
                        shows.update {
                            PagingDataUiModel(
                                items = response.items,
                                totalCount = response.totalElements,
                                currentPage = 0,
                                totalPages = response.totalPages,
                                hasNext = response.hasNext,
                            )
                        }
                    }
                },
                async {
                    searchRepository.searchProfiles(keyword, 0).onSuccess { response ->
                        profiles.update {
                            PagingDataUiModel(
                                items = response.items,
                                totalCount = response.totalElements,
                                currentPage = 0,
                                totalPages = response.totalPages,
                                hasNext = response.hasNext,
                            )
                        }
                    }
                },
                async {
                    searchRepository.searchVenues(keyword, 0).onSuccess { response ->
                        venues.update {
                            PagingDataUiModel(
                                items = response.items,
                                totalCount = response.totalElements,
                                currentPage = 0,
                                totalPages = response.totalPages,
                                hasNext = response.hasNext,
                            )
                        }
                    }
                },
            ).awaitAll()

            _uiState.update {
                it.copy(
                    searchedKeyword = keyword,
                    loading = false,
                )
            }
        }
    }

    private fun changeTabIndex(index: Int) {
        _uiState.update { it.copy(tabIndex = index.takeIf { i -> i < 4 } ?: 0) }
    }

    private fun loadNextShowsPage() {
        if (searchShowsJob?.isActive == true || !shows.value.hasNext) return
        _uiState.update { it.copy(showsLoading = true) }

        searchShowsJob = viewModelScope.launch {
            val currentPage = shows.value.currentPage
            val nextPage = currentPage + 1
            val currentItems = shows.value.items

            searchRepository.searchShows(uiState.value.searchedKeyword, nextPage).onSuccess { searchResult ->
                if (searchResult.items.isNotEmpty()) {
                    val appendedItems = (currentItems + searchResult.items).distinctBy { it.id }
                    shows.update {
                        it.copy(
                            items = appendedItems,
                            totalCount = searchResult.totalElements,
                            currentPage = nextPage,
                            totalPages = searchResult.totalPages,
                            hasNext = searchResult.hasNext,
                        )
                    }
                }
            }
            _uiState.update { it.copy(showsLoading = false) }
        }
    }

    private fun loadNextProfilesPage() {
        if (searchProfilesJob?.isActive == true || !profiles.value.hasNext) return
        _uiState.update { it.copy(profilesLoading = true) }

        searchProfilesJob = viewModelScope.launch {
            val currentPage = profiles.value.currentPage
            val nextPage = currentPage + 1
            val currentItems = profiles.value.items

            searchRepository.searchProfiles(uiState.value.searchedKeyword, nextPage).onSuccess { searchResult ->
                if (searchResult.items.isNotEmpty()) {
                    val appendedItems = (currentItems + searchResult.items).distinctBy { it.userCode }
                    profiles.update {
                        it.copy(
                            items = appendedItems,
                            totalCount = searchResult.totalElements,
                            currentPage = nextPage,
                            totalPages = searchResult.totalPages,
                            hasNext = searchResult.hasNext,
                        )
                    }
                }
            }
            _uiState.update { it.copy(profilesLoading = false) }
        }
    }

    private fun loadNextVenuesPage() {
        if (searchVenuesJob?.isActive == true || !venues.value.hasNext) return
        _uiState.update { it.copy(venuesLoading = true) }

        searchVenuesJob = viewModelScope.launch {
            val currentPage = venues.value.currentPage
            val nextPage = currentPage + 1
            val currentItems = venues.value.items

            searchRepository.searchVenues(uiState.value.searchedKeyword, nextPage).onSuccess { searchResult ->
                if (searchResult.items.isNotEmpty()) {
                    val appendedItems = (currentItems + searchResult.items).distinctBy { it.id }
                    venues.update {
                        it.copy(
                            items = appendedItems,
                            totalCount = searchResult.totalElements,
                            currentPage = nextPage,
                            totalPages = searchResult.totalPages,
                            hasNext = searchResult.hasNext,
                        )
                    }
                }
            }
            _uiState.update { it.copy(venuesLoading = false) }
        }
    }
}
