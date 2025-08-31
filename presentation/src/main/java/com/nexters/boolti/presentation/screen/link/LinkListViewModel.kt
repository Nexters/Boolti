package com.nexters.boolti.presentation.screen.link

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.nexters.boolti.domain.model.Link
import com.nexters.boolti.domain.repository.MemberRepository
import com.nexters.boolti.domain.repository.UserConfigRepository
import com.nexters.boolti.domain.usecase.GetUserUsecase
import com.nexters.boolti.presentation.screen.navigation.LinkListRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LinkListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getUserUseCase: GetUserUsecase,
    private val memberRepository: MemberRepository,
    private val userConfigRepository: UserConfigRepository,
) : ViewModel() {
    private val userCode = savedStateHandle.toRoute<LinkListRoute.LinkListRoot>().userCode
    private val isMine = getUserUseCase()?.userCode == userCode
    private var autoNavigatedToEdit = false

    private val _uiState = MutableStateFlow(
        LinkListState(isMine = isMine)
    )
    val uiState = _uiState.asStateFlow()

    private val _linkListEvent = Channel<LinkListEvent>()
    val linkListEvent = _linkListEvent.receiveAsFlow()

    private val _linkEditEvent = Channel<LinkEditEvent>()
    val linkEditEvent = _linkEditEvent.receiveAsFlow()

    init {
        fetchLinks()
    }

    private fun fetchLinks() {
        viewModelScope.launch {
            memberRepository.getLinks(userCode, refresh = true)
                .onSuccess { links ->
                    _uiState.update {
                        it.copy(
                            links = links,
                            originalLinks = links,
                            editing = isMine && links.isEmpty(),
                        )
                    }
                    if (isMine && links.isEmpty()) {
                        autoNavigatedToEdit = true
                        linkListEvent(LinkListEvent.NavigateToEdit)
                    }
                }
                .onFailure {
                    // TODO 에러 처리
                }
        }
    }

    fun save() {
        _uiState.update { it.copy(saving = true) }
        viewModelScope.launch {
            userConfigRepository.saveLinks(
                uiState.value.links,
            ).onSuccess {
                _uiState.update {
                    it.copy(
                        saving = false,
                        editing = false,
                        editingLink = null,
                        originalLinks = uiState.value.links,
                        showExitAlertDialog = false,
                    )
                }
            }
        }
    }

    fun tryBack() {
        when {
            uiState.value.editing && autoNavigatedToEdit && !uiState.value.saveEnabled -> {
                linkEditEvent(LinkEditEvent.Finish)
                linkListEvent(LinkListEvent.Finish)
            }

            uiState.value.editing && uiState.value.edited -> {
                _uiState.update { it.copy(showExitAlertDialog = true) }
            }

            uiState.value.editing && uiState.value.originalLinks.isEmpty() -> {
                linkListEvent(LinkListEvent.Finish)
            }

            uiState.value.editing -> {
                _uiState.update { it.copy(editing = false) }
            }

            else -> {
                linkListEvent(LinkListEvent.Finish)
            }
        }
    }

    fun startAddOrEditLink(
        linkId: String?,
    ) {
        val targetLink = uiState.value.links.find { it.id == linkId }
            ?: Link(
                id = "",
                name = "",
                url = "",
            )
        _uiState.update { it.copy(editingLink = targetLink) }
    }

    fun onLinkNameChanged(
        name: String,
    ) {
        if (uiState.value.editingLink == null) startAddOrEditLink(null)

        _uiState.update {
            it.copy(
                editingLink = it.editingLink?.copy(name = name)
            )
        }
    }

    fun onLinkUrlChanged(
        url: String,
    ) {
        if (uiState.value.editingLink == null) startAddOrEditLink(null)

        _uiState.update {
            it.copy(
                editingLink = it.editingLink?.copy(url = url)
            )
        }
    }

    fun removeLink() {
        val targetLink = uiState.value.editingLink ?: return
        _uiState.update {
            it.copy(
                links = it.links.filterNot { link -> link.id == targetLink.id },
                editingLink = null,
            )
        }
        autoNavigatedToEdit = false
        linkEditEvent(LinkEditEvent.Finish)
        linkListEvent(LinkListEvent.Removed)
    }

    fun completeAddOrEditLink() {
        val link = uiState.value.editingLink ?: return
        val editMode = link.id.isNotEmpty()
        if (editMode) {
            editLink(link)
        } else {
            addLink(link)
        }
        autoNavigatedToEdit = false
        linkEditEvent(LinkEditEvent.Finish)
        linkListEvent(
            if (editMode) LinkListEvent.Edited else LinkListEvent.Added,
        )
    }

    private fun addLink(
        link: Link,
    ) {
        val newLink = link.copy(id = UUID.randomUUID().toString())
        _uiState.update {
            it.copy(
                links = listOf(newLink) + it.links, // 최상단에 추가
                editingLink = null,
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
                editingLink = null,
            )
        }
    }

    fun reorder(from: Int, to: Int) {
        val links = uiState.value.links.toMutableList()
        if (from !in links.indices || to !in links.indices) return

        _uiState.update {
            it.copy(
                links = links.apply { add(to, removeAt(from)) },
            )
        }
    }

    fun setEditMode() {
        _uiState.update { it.copy(editing = true) }
    }

    fun disMissExitAlertDialog() {
        _uiState.update { it.copy(showExitAlertDialog = false) }
    }

    private fun linkListEvent(event: LinkListEvent) {
        viewModelScope.launch {
            _linkListEvent.send(event)
        }
    }

    private fun linkEditEvent(event: LinkEditEvent) {
        viewModelScope.launch {
            _linkEditEvent.send(event)
        }
    }
}
