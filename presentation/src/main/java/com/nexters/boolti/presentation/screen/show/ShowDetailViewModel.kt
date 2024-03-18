package com.nexters.boolti.presentation.screen.show

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.repository.AuthRepository
import com.nexters.boolti.domain.repository.ShowRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShowDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val showRepository: ShowRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val showId: String = checkNotNull(savedStateHandle["showId"])

    private val _uiState = MutableStateFlow(ShowDetailUiState())
    val uiState: StateFlow<ShowDetailUiState> = _uiState.asStateFlow()

    private val _events = Channel<ShowDetailEvent>()
    val events: Flow<ShowDetailEvent> = _events.receiveAsFlow()

    val loggedIn = authRepository.loggedIn.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null,
    )

    init {
        fetchShowDetail()
    }

    fun sendEvent(event: ShowDetailEvent) = viewModelScope.launch { _events.send(event) }

    private fun fetchShowDetail() {
        viewModelScope.launch {
            showRepository.searchById(id = showId)
                .onSuccess { newShowDetail ->
                    _uiState.update { it.copy(showDetail = newShowDetail) }
                }
                .onFailure {
                    it.printStackTrace()
                    // todo : 예외 처리
                }
        }
    }

    fun preventEvents() {
        _events.cancel()
    }
}