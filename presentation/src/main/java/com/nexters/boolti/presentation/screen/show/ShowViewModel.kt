package com.nexters.boolti.presentation.screen.show

import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.model.PopupResult
import com.nexters.boolti.domain.model.Show
import com.nexters.boolti.domain.model.User
import com.nexters.boolti.domain.repository.AuthRepository
import com.nexters.boolti.domain.repository.PopupRepository
import com.nexters.boolti.domain.repository.ShowRepository
import com.nexters.boolti.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ShowViewModel @Inject constructor(
    private val showRepository: ShowRepository,
    private val popupRepository: PopupRepository,
    authRepository: AuthRepository,
) : BaseViewModel() {
    val user: StateFlow<User.My?> = authRepository.cachedUser.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null,
    )

    private val _uiState = MutableStateFlow(ShowUiState())
    val uiState: StateFlow<ShowUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ShowEvent>()
    val events: SharedFlow<ShowEvent> = _events.asSharedFlow()

    private var loadShowsJob: Job? = null

    init {
        loadShows()
        fetchPopup()
    }

    private fun sendEvent(event: ShowEvent) {
        viewModelScope.launch {
            _events.emit(event)
        }
    }

    fun refresh() = loadShows()

    private fun loadShows() {
        loadShowsJob?.cancel()

        _uiState.update {
            it.copy(isRefreshing = true)
        }

        loadShowsJob = viewModelScope.launch {
            showRepository.search("").onSuccess { shows ->
                _uiState.update {
                    it.copy(
                        shows = getInsertedBannerShows(shows),
                        isRefreshing = false,
                    )
                }
            }.onFailure { throwable ->
                Timber.e(throwable)
                _uiState.update {
                    it.copy(
                        isRefreshing = false,
                    )
                }
            }
        }
    }

    private fun getInsertedBannerShows(shows: List<Show>): List<ShowListItem> {
        return buildList {
            addAll(shows.take(4).map { it.toUI() })
            if (shows.isNotEmpty()) add(ShowListItem.BannerItem)
            addAll(shows.drop(4).map { it.toUI() })
        }
    }

    private fun fetchPopup() {
        viewModelScope.launch(recordExceptionHandler) {
            when (val result = popupRepository.getPopup("HOME")) {
                is PopupResult.None -> Timber.d("홈 팝업 없음")
                is PopupResult.Hidden -> Timber.d("오늘 숨긴 이벤트 팝업: ${result.popup.id}")
                is PopupResult.Show -> sendEvent(ShowEvent.ShowPopup(result.popup))
            }
        }
    }

    fun hideEventToday(popupId: String) {
        popupRepository
            .hideEventToday(popupId)
            .launchIn(viewModelScope + recordExceptionHandler)
    }
}
