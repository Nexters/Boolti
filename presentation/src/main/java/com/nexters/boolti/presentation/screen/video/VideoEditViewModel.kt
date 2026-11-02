package com.nexters.boolti.presentation.screen.video

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.nexters.boolti.presentation.screen.navigation.VideoListRoute
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
class VideoEditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val route = savedStateHandle.toRoute<VideoListRoute.VideoEdit>()

    private val _uiState = MutableStateFlow(
        VideoEditUiState(
            isEditMode = route.localId != null,
            url = route.url,
            originalUrl = route.url,
        )
    )
    val uiState: StateFlow<VideoEditUiState> = _uiState.asStateFlow()

    private val _event = Channel<VideoEditEvent>(Channel.BUFFERED)
    val event: Flow<VideoEditEvent> = _event.receiveAsFlow()

    fun onAction(action: VideoEditAction) {
        when (action) {
            VideoEditAction.Back -> sendEvent(
                if (route.closeListOnBack) VideoEditEvent.CloseWithList else VideoEditEvent.Close,
            )
            is VideoEditAction.ChangeUrl -> _uiState.update { it.copy(url = action.url) }
            VideoEditAction.Complete -> complete()
            VideoEditAction.Remove -> route.localId?.let { sendEvent(VideoEditEvent.Done(VideoEditResult.Removed(it))) }
        }
    }

    private fun complete() {
        val url = uiState.value.url
        val result = route.localId
            ?.let { VideoEditResult.Edited(localId = it, url = url) }
            ?: VideoEditResult.Added(url = url)
        sendEvent(VideoEditEvent.Done(result))
    }

    private fun sendEvent(event: VideoEditEvent) {
        viewModelScope.launch {
            _event.send(event)
        }
    }
}
