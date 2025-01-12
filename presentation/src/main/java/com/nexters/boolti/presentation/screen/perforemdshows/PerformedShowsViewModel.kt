package com.nexters.boolti.presentation.screen.perforemdshows

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.nexters.boolti.domain.repository.MemberRepository
import com.nexters.boolti.domain.usecase.GetUserUsecase
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
    private val getUserUsecase: GetUserUsecase,
) : BaseViewModel() {
    private val _uiState = MutableStateFlow(PerformedShowsState())
    val uiState = _uiState.asStateFlow()

    private val _event = Channel<PerformedShowsEvent>()
    val event = _event.receiveAsFlow()

    init {
        initLoad()
    }

    private fun initLoad() {
        savedStateHandle.toRoute<MainRoute.PerformedShows>().userCode?.let { userCode ->
            fetchOthersShows(userCode)
        } ?: fetchMyShows()
    }

    private fun fetchMyShows() {
        _uiState.update { it.copy(loading = true) }
        getUserUsecase()?.performedShow?.let { shows ->
            _uiState.update {
                it.copy(
                    loading = false,
                    shows = shows,
                )
            }
        } ?: event(PerformedShowsEvent.FetchFailed)
    }

    private fun fetchOthersShows(userCode: String) {
        _uiState.update { it.copy(loading = true) }
        viewModelScope.launch(recordExceptionHandler) {
            memberRepository.getMember(userCode)
                .onSuccess { member ->
                    _uiState.update {
                        it.copy(
                            loading = false,
                            shows = member.performedShow,
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
