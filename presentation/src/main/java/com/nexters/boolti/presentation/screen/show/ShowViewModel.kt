package com.nexters.boolti.presentation.screen.show

import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.model.Show
import com.nexters.boolti.domain.model.User
import com.nexters.boolti.domain.repository.AuthRepository
import com.nexters.boolti.domain.repository.PopupRepository
import com.nexters.boolti.domain.repository.ShowRepository
import com.nexters.boolti.domain.usecase.GetPopupUseCase
import com.nexters.boolti.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
    private val getPopupUseCase: GetPopupUseCase,
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

    init {
        loadShows()
        fetchPopup()
    }

    private fun sendEvent(event: ShowEvent) {
        viewModelScope.launch {
            _events.emit(event)
        }
    }

    private fun loadShows() {
        viewModelScope.launch {
            showRepository.search("").onSuccess { shows ->
                _uiState.update {
                    it.copy(shows = getInsertedBannerShows(shows))
                }
            }.onFailure {
                Timber.e(it)
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
        getPopupUseCase("HOME")
            .onEach { popup -> sendEvent(ShowEvent.ShowPopup(popup)) }
            .launchIn(viewModelScope + recordExceptionHandler)
    }

    fun hideEventToday(popupId: String) {
        popupRepository
            .hideEventToday(popupId)
            .launchIn(viewModelScope + recordExceptionHandler)
    }
}
