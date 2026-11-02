package com.nexters.boolti.presentation.screen.link

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.nexters.boolti.domain.model.Link
import com.nexters.boolti.domain.repository.MemberRepository
import com.nexters.boolti.domain.repository.UserConfigRepository
import com.nexters.boolti.domain.usecase.GetCachedUserUseCase
import com.nexters.boolti.presentation.screen.navigation.LinkListRoute
import com.nexters.boolti.presentation.screen.profileedit.link.LinkEditResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LinkListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getCachedUserUseCase: GetCachedUserUseCase,
    private val memberRepository: MemberRepository,
    private val userConfigRepository: UserConfigRepository,
) : ViewModel() {
    private val route = savedStateHandle.toRoute<LinkListRoute.LinkList>()
    private val userCode = route.userCode
    private val isMine = getCachedUserUseCase()?.userCode == userCode
    private val isEditModeAtFirst = route.isEditMode && isMine
    private var autoNavigatedToEdit = isEditModeAtFirst

    private val _uiState = MutableStateFlow(
        LinkListUiState(
            isMine = isMine,
            editing = isEditModeAtFirst,
        )
    )
    val uiState: StateFlow<LinkListUiState> = _uiState.asStateFlow()

    private val _event = Channel<LinkListEvent>(Channel.BUFFERED)
    val event: Flow<LinkListEvent> = _event.receiveAsFlow()

    init {
        fetchLinks()
    }

    fun onAction(action: LinkListAction) {
        when (action) {
            LinkListAction.Back -> tryBack()
            LinkListAction.Exit -> sendEvent(LinkListEvent.Finish)
            LinkListAction.Save -> save()
            LinkListAction.StartEditing -> _uiState.update { it.copy(editing = true) }
            LinkListAction.DismissExitAlertDialog -> _uiState.update { it.copy(showExitAlertDialog = false) }
            is LinkListAction.Reorder -> reorder(action.from, action.to)
            LinkListAction.ClickAddLink -> sendEvent(LinkListEvent.NavigateToAddLink(closeListOnBack = false))
            is LinkListAction.ClickLink -> uiState.value.links
                .find { it.id == action.linkId }
                ?.let { sendEvent(LinkListEvent.NavigateToEditLink(it)) }
            is LinkListAction.EditResultReceived -> applyEditResult(action.result)
        }
    }

    private fun fetchLinks() {
        viewModelScope.launch {
            memberRepository.getLinks(userCode)
                .onSuccess { links ->
                    _uiState.update {
                        it.copy(
                            links = links,
                            originalLinks = links,
                            editing = it.editing || isMine && links.isEmpty(),
                        )
                    }
                    if (isMine && links.isEmpty()) {
                        autoNavigatedToEdit = true
                        sendEvent(LinkListEvent.NavigateToAddLink(closeListOnBack = true))
                    }
                }
                .onFailure {
                    // TODO 에러 처리
                }
        }
    }

    private fun applyEditResult(result: LinkEditResult) {
        when (result) {
            is LinkEditResult.Added -> {
                addLink(Link(id = "", name = result.name, url = result.url))
                sendEvent(LinkListEvent.Added)
            }

            is LinkEditResult.Edited -> {
                editLink(Link(id = result.id, name = result.name, url = result.url))
                sendEvent(LinkListEvent.Edited)
            }

            is LinkEditResult.Removed -> {
                _uiState.update { it.copy(links = it.links.filterNot { link -> link.id == result.id }) }
                sendEvent(LinkListEvent.Removed)
            }
        }
        autoNavigatedToEdit = false
    }

    private fun save() {
        _uiState.update { it.copy(saving = true) }
        viewModelScope.launch {
            userConfigRepository.saveLinks(
                uiState.value.links,
            ).onSuccess {
                _uiState.update {
                    it.copy(
                        saving = false,
                        editing = false,
                        originalLinks = uiState.value.links,
                        showExitAlertDialog = false,
                    )
                }
            }
        }
    }

    private fun tryBack() {
        when {
            uiState.value.editing && autoNavigatedToEdit && !uiState.value.saveEnabled -> {
                sendEvent(LinkListEvent.Finish)
            }

            uiState.value.editing && uiState.value.edited -> {
                _uiState.update { it.copy(showExitAlertDialog = true) }
            }

            uiState.value.editing && uiState.value.originalLinks.isEmpty() -> {
                sendEvent(LinkListEvent.Finish)
            }

            uiState.value.editing -> {
                _uiState.update { it.copy(editing = false) }
            }

            else -> {
                sendEvent(LinkListEvent.Finish)
            }
        }
    }

    private fun addLink(
        link: Link,
    ) {
        val newLink = link.copy(id = UUID.randomUUID().toString())
        _uiState.update {
            it.copy(
                links = listOf(newLink) + it.links, // 최상단에 추가
            )
        }
    }

    private fun editLink(
        link: Link,
    ) {
        _uiState.update {
            it.copy(
                links = it.links.map { old ->
                    if (old.id == link.id) link else old
                },
            )
        }
    }

    private fun reorder(from: Int, to: Int) {
        val links = uiState.value.links.toMutableList()
        if (from !in links.indices || to !in links.indices) return

        _uiState.update {
            it.copy(
                links = links.apply { add(to, removeAt(from)) },
            )
        }
    }

    private fun sendEvent(event: LinkListEvent) {
        viewModelScope.launch {
            _event.send(event)
        }
    }
}
