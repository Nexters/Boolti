package com.nexters.boolti.presentation.screen.link

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
class LinkListViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val memberRepository: MemberRepository,
) : BaseViewModel() {
    private val _uiState = MutableStateFlow(LinkListState())
    val uiState = _uiState.asStateFlow()

    private val _event = Channel<LinkListEvent>()
    val event = _event.receiveAsFlow()

    init {
        initLoad()
    }

    private fun initLoad() {
        savedStateHandle.toRoute<MainRoute.LinkList>().userCode.let(::fetchLinks)
    }

    private fun fetchLinks(userCode: UserCode) {
        viewModelScope.launch(recordExceptionHandler) {
            memberRepository.getLinks(userCode).onSuccess { links ->
                _uiState.update {
                    it.copy(
                        loading = false,
                        links = links,
                    )
                }
            }.onFailure {
                _uiState.update {
                    it.copy(loading = false)
                }
                event(LinkListEvent.LoadFailed)
            }
        }
    }

    private fun event(event: LinkListEvent) {
        viewModelScope.launch {
            _event.send(event)
        }
    }
}
