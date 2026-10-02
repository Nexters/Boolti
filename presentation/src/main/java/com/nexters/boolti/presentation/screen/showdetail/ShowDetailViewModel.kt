package com.nexters.boolti.presentation.screen.showdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.nexters.boolti.domain.model.ShowDetail
import com.nexters.boolti.domain.model.ShowState
import com.nexters.boolti.domain.repository.AuthRepository
import com.nexters.boolti.domain.repository.PopupRepository
import com.nexters.boolti.domain.repository.ShowRepository
import com.nexters.boolti.presentation.extension.stateInUi
import com.nexters.boolti.presentation.screen.navigation.ShowRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.Duration
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.toKotlinDuration

@HiltViewModel
class ShowDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val showRepository: ShowRepository,
    private val popupRepository: PopupRepository,
    authRepository: AuthRepository,
) : ViewModel() {
    private val route = checkNotNull(savedStateHandle.toRoute<ShowRoute.ShowRoot>())
    val showId: String = route.showId
    val source: String = route.source

    private val _uiState = MutableStateFlow(ShowDetailUiState())

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<ShowDetailUiState> = _uiState
        .flatMapLatest { state ->
            state.showDetail?.let { detail -> showStateFlow(detail).map { state.copy(showState = it) } }
                ?: flowOf(state)
        }
        .stateInUi(viewModelScope, _uiState.value)

    private val _events = Channel<ShowDetailEvent>()
    val events: Flow<ShowDetailEvent> = _events.receiveAsFlow()

    val loggedIn = authRepository.loggedIn.stateInUi(viewModelScope, null)

    init {
        fetchShowDetail()
        fetchCastTeams()
        fetchShoulShowNaverMapDialog()
    }

    /** 상태가 바뀌는 시각에만 다시 계산한다. 다시 구독하면 처음부터 계산해서 기기가 잠든 동안 밀린 delay를 바로잡는다 */
    private fun showStateFlow(detail: ShowDetail): Flow<ShowState> = flow {
        while (true) {
            val now = LocalDateTime.now()
            emit(detail.state(now))
            val next = detail.nextStateChangeAt(now) ?: break
            delay(Duration.between(now, next).toKotlinDuration() + 1.milliseconds)
        }
    }

    fun sendEvent(event: ShowDetailEvent) = viewModelScope.launch { _events.send(event) }

    private fun fetchShowDetail() {
        viewModelScope.launch {
            showRepository.searchById(id = showId)
                .onSuccess { newShowDetail ->
                    _uiState.update { it.copy(showDetail = newShowDetail, isLoading = false) }
                }
                .onFailure {
                    _uiState.update { it.copy(isLoading = false) }
                    Firebase.crashlytics.recordException(it)
                    Timber.e(it)
                }
        }
    }

    private fun fetchCastTeams() {
        viewModelScope.launch {
            showRepository.requestCastTeams(showId = showId)
                .onSuccess { newCastTeams ->
                    _uiState.update {
                        it.copy(castTeams = newCastTeams)
                    }
                }
                .onFailure {
                    Firebase.crashlytics.recordException(it)
                    Timber.e(it)
                }
        }
    }

    private fun fetchShoulShowNaverMapDialog() {
        popupRepository.shouldShowNaverMapDialog()
            .onEach { shouldShow ->
                _uiState.update {
                    it.copy(shouldShowNaverMapDialog = shouldShow)
                }
            }
            .launchIn(viewModelScope)
    }

    fun doNotShowNaverMapDialogAnymore() {
        viewModelScope.launch {
            popupRepository.doNotShowNaverMapPopupAnyMore()
        }
    }

    fun selectTab(index: Int) {
        _uiState.update { it.copy(selectedTab = index.coerceIn(0..1)) }
    }

    fun preventEvents() {
        _events.cancel()
    }
}
