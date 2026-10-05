package com.nexters.boolti.presentation.screen.profileedit.link

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.nexters.boolti.presentation.screen.navigation.LinkListRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LinkEditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val route = savedStateHandle.toRoute<LinkListRoute.LinkEdit>()

    private val _uiState = MutableStateFlow(
        LinkEditUiState(
            isEditMode = route.linkId != null,
            name = route.name,
            url = route.url,
        )
    )
    val uiState: StateFlow<LinkEditUiState> = _uiState.asStateFlow()

    private val _event = Channel<LinkEditEvent>(Channel.BUFFERED)
    val event: Flow<LinkEditEvent> = _event.receiveAsFlow()

    fun onAction(action: LinkEditAction) {
        when (action) {
            LinkEditAction.Back -> sendEvent(
                if (route.closeListOnBack) LinkEditEvent.CloseWithList else LinkEditEvent.Close,
            )
            is LinkEditAction.ChangeName -> _uiState.update { it.copy(name = action.name) }
            is LinkEditAction.ChangeUrl -> _uiState.update { it.copy(url = action.url) }
            LinkEditAction.Complete -> complete()
            LinkEditAction.Remove -> route.linkId?.let { sendEvent(LinkEditEvent.Done(LinkEditResult.Removed(it))) }
        }
    }

    private fun complete() {
        val state = uiState.value
        val result = route.linkId
            ?.let { LinkEditResult.Edited(id = it, name = state.name, url = state.url) }
            ?: LinkEditResult.Added(name = state.name, url = state.url)
        sendEvent(LinkEditEvent.Done(result))
    }

    private fun sendEvent(event: LinkEditEvent) {
        viewModelScope.launch {
            _event.send(event)
        }
    }
}
