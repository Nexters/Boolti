package com.nexters.boolti.presentation.screen.video

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.nexters.boolti.domain.model.YouTubeVideo
import com.nexters.boolti.domain.repository.UserConfigRepository
import com.nexters.boolti.domain.usecase.GetCachedUserUseCase
import com.nexters.boolti.domain.usecase.GetYouTubeVideoInfoByUrlUseCase
import com.nexters.boolti.domain.usecase.GetYouTubeVideoListByUserCodeUseCase
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
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class VideoListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getCachedUserUseCase: GetCachedUserUseCase,
    private val getYouTubeVideoListByUserCodeUseCase: GetYouTubeVideoListByUserCodeUseCase,
    private val getYouTubeVideoInfoByUrlUseCase: GetYouTubeVideoInfoByUrlUseCase,
    private val userConfigRepository: UserConfigRepository,
) : ViewModel() {
    private val route = savedStateHandle.toRoute<VideoListRoute.VideoList>()
    private val userCode = route.userCode
    private val isMine = getCachedUserUseCase()?.userCode == userCode
    private val isEditModeAtFirst = route.isEditMode && isMine
    private var autoNavigatedToEdit = isEditModeAtFirst

    private val _uiState = MutableStateFlow(
        VideoListUiState(
            isMine = isMine,
            loading = true,
            editing = isEditModeAtFirst,
        )
    )
    val uiState: StateFlow<VideoListUiState> = _uiState.asStateFlow()

    private val _event = Channel<VideoListEvent>(Channel.BUFFERED)
    val event: Flow<VideoListEvent> = _event.receiveAsFlow()

    init {
        fetchVideos()
    }

    fun onAction(action: VideoListAction) {
        when (action) {
            VideoListAction.Back -> tryBack()
            VideoListAction.Exit -> sendEvent(VideoListEvent.Finish)
            VideoListAction.Save -> save()
            VideoListAction.StartEditing -> _uiState.update { it.copy(editing = true) }
            VideoListAction.DismissExitAlertDialog -> _uiState.update { it.copy(showExitAlertDialog = false) }
            is VideoListAction.Reorder -> reorder(action.from, action.to)
            VideoListAction.ClickAddVideo -> sendEvent(VideoListEvent.NavigateToAddVideo(closeListOnBack = false))
            is VideoListAction.ClickVideo -> uiState.value.videos
                .find { it.localId == action.localId }
                ?.let { sendEvent(VideoListEvent.NavigateToEditVideo(it)) }
            is VideoListAction.EditResultReceived -> viewModelScope.launch { applyEditResult(action.result) }
        }
    }

    private fun fetchVideos() {
        viewModelScope.launch {
            getYouTubeVideoListByUserCodeUseCase(userCode)
                .onSuccess { videos ->
                    _uiState.update {
                        it.copy(
                            videos = videos,
                            originalVideos = videos,
                            editing = it.editing || isMine && videos.isEmpty(),
                            loading = false,
                        )
                    }
                    if (isMine && videos.isEmpty()) {
                        autoNavigatedToEdit = true
                        sendEvent(VideoListEvent.NavigateToAddVideo(closeListOnBack = true))
                    }
                }
                .onFailure {
                    _uiState.update { it.copy(loading = false) }
                    // TODO 에러 처리
                }
        }
    }

    private suspend fun applyEditResult(result: VideoEditResult) {
        autoNavigatedToEdit = false
        when (result) {
            is VideoEditResult.Added -> {
                // 유효하지 않은 URL이면 주소만 가진 동영상으로 추가
                val video = getYouTubeVideoInfoByUrlUseCase(result.url) ?: createInvalidVideo(result.url)
                addVideo(video)
                sendEvent(VideoListEvent.Added)
            }

            is VideoEditResult.Edited -> {
                val video = getYouTubeVideoInfoByUrlUseCase(result.url) ?: createInvalidVideo(result.url)
                editVideo(video.copy(localId = result.localId))
                sendEvent(VideoListEvent.Edited)
            }

            is VideoEditResult.Removed -> {
                _uiState.update { it.copy(videos = it.videos.filterNot { video -> video.localId == result.localId }) }
                sendEvent(VideoListEvent.Removed)
            }
        }
    }

    private fun save() {
        _uiState.update { it.copy(saving = true) }
        viewModelScope.launch {
            userConfigRepository.saveVideos(
                uiState.value.videos.map { it.url },
            ).onSuccess {
                _uiState.update {
                    it.copy(
                        saving = false,
                        editing = false,
                        originalVideos = uiState.value.videos,
                        showExitAlertDialog = false,
                    )
                }
            }
        }
    }

    private fun tryBack() {
        when {
            uiState.value.editing && autoNavigatedToEdit && !uiState.value.saveEnabled -> {
                sendEvent(VideoListEvent.Finish)
            }

            uiState.value.editing && uiState.value.edited -> {
                _uiState.update { it.copy(showExitAlertDialog = true) }
            }

            uiState.value.editing && uiState.value.originalVideos.isEmpty() -> {
                sendEvent(VideoListEvent.Finish)
            }

            uiState.value.editing -> {
                _uiState.update { it.copy(editing = false) }
            }

            else -> {
                sendEvent(VideoListEvent.Finish)
            }
        }
    }

    private fun addVideo(
        video: YouTubeVideo,
    ) {
        val newVideo = video.copy(localId = UUID.randomUUID().toString())
        _uiState.update {
            it.copy(
                videos = listOf(newVideo) + it.videos, // 최상단에 추가
            )
        }
    }

    private fun editVideo(
        video: YouTubeVideo,
    ) {
        _uiState.update {
            it.copy(
                videos = it.videos.map { old ->
                    if (old.localId == video.localId) video else old
                },
            )
        }
    }

    private fun reorder(from: Int, to: Int) {
        val videos = uiState.value.videos.toMutableList()
        if (from !in videos.indices || to !in videos.indices) return

        _uiState.update {
            it.copy(
                videos = videos.apply { add(to, removeAt(from)) },
            )
        }
    }

    private fun sendEvent(event: VideoListEvent) {
        viewModelScope.launch {
            _event.send(event)
        }
    }

    private fun createInvalidVideo(url: String): YouTubeVideo {
        return YouTubeVideo.EMPTY.copy(
            localId = UUID.randomUUID().toString(),
            url = url,
        )
    }
}
