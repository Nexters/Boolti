package com.nexters.boolti.presentation.screen.perforemdshows

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.nexters.boolti.domain.model.UserCode
import com.nexters.boolti.domain.repository.MemberRepository
import com.nexters.boolti.presentation.base.BaseViewModel
import com.nexters.boolti.presentation.screen.navigation.MainRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PerformedShowsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val memberRepository: MemberRepository,
) : BaseViewModel() {
    private val _uiState = MutableStateFlow(PerformedShowsState())
    val uiState = _uiState.asStateFlow()

    private val _event = Channel<PerformedShowsEvent>()
    val event = _event.receiveAsFlow()

    init {
        initLoad()
    }

    private fun initLoad() {
        savedStateHandle.toRoute<MainRoute.PerformedShows>().userCode.let(::fetchShows)
    }

    private fun fetchShows(userCode: UserCode) {
        _uiState.update { it.copy(loading = true) }
        viewModelScope.launch(recordExceptionHandler) {
            memberRepository.getPerformedShows(userCode)
                .onSuccess { shows ->
                    _uiState.update {
                        it.copy(
                            loading = false,
                            shows = shows,
                        )
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(loading = false)
                    }
                    event(PerformedShowsEvent.FetchFailed)
                }
        }
    }

    private fun event(event: PerformedShowsEvent) {
        viewModelScope.launch {
            _event.send(event)
        }
    }
}
