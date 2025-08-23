package com.nexters.boolti.presentation.screen.video

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.nexters.boolti.domain.model.YouTubeVideo
import com.nexters.boolti.domain.repository.MemberRepository
import com.nexters.boolti.domain.repository.UserConfigRepository
import com.nexters.boolti.domain.usecase.GetUserUsecase
import com.nexters.boolti.domain.usecase.GetYouTubeVideoInfoByUrlListUseCase
import com.nexters.boolti.presentation.screen.navigation.VideoListRoute
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
class VideoListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getUserUseCase: GetUserUsecase,
    private val memberRepository: MemberRepository,
    private val getYouTubeVideoInfoByUrlListUseCase: GetYouTubeVideoInfoByUrlListUseCase,
    private val userConfigRepository: UserConfigRepository,
) : ViewModel() {
    private val userCode = savedStateHandle.toRoute<VideoListRoute.VideoListRoot>().userCode
    private val isMine = getUserUseCase()?.userCode == userCode

    private val _uiState = MutableStateFlow(
        VideoListState(isMine = isMine)
    )
    val uiState = _uiState.asStateFlow()

    private val _videoListEvent = Channel<VideoListEvent>()
    val videoListEvent = _videoListEvent.receiveAsFlow()

    private val _videoEditEvent = Channel<VideoEditEvent>()
    val videoEditEvent = _videoEditEvent.receiveAsFlow()

    init {
        fetchVideos()
    }

    private fun fetchVideos() {
        viewModelScope.launch {
            memberRepository.getVideoLinks(userCode, refresh = true)
                .onSuccess { videos ->
                    _uiState.update {
                        it.copy(
                            videos = videos,
                            originalVideos = videos,
                        )
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
            userConfigRepository.saveVideos(
                uiState.value.videos.map { it.url },
            ).onSuccess {
                _uiState.update {
                    it.copy(
                        saving = false,
                        editing = false,
                        editingVideo = null,
                        originalVideos = uiState.value.videos,
                        showExitAlertDialog = false,
                    )
                }
                videoListEvent(VideoListEvent.Finish)
            }
        }
    }

    fun tryBack() {
        when {
            uiState.value.editing && uiState.value.edited -> {
                _uiState.update { it.copy(showExitAlertDialog = true) }
            }

            uiState.value.editing -> {
                _uiState.update { it.copy(editing = false) }
            }

            else -> {
                videoListEvent(VideoListEvent.Finish)
            }
        }
    }

    fun startAddOrEditVideo(
        videoId: String?,
    ) {
        val targetVideo = uiState.value.videos.find { it.id == videoId }
            ?: YouTubeVideo.EMPTY
        _uiState.update { it.copy(editingVideo = targetVideo) }
    }

    fun onVideoUrlChanged(
        url: String,
    ) {
        if (uiState.value.editingVideo == null) startAddOrEditVideo(null)

        _uiState.update {
            it.copy(
                editingVideo = it.editingVideo?.copy(url = url)
            )
        }
    }

    fun removeVideo() {
        val targetVideo = uiState.value.editingVideo ?: return
        _uiState.update {
            it.copy(
                videos = it.videos.filterNot { it.id == targetVideo.id },
                editingVideo = null,
            )
        }
        videoEditEvent(VideoEditEvent.Finish)
        videoListEvent(VideoListEvent.Removed)
    }

    fun completeAddOrEditVideo() {
        val video = uiState.value.editingVideo ?: return
        val editMode = video.id.isNotEmpty()
        if (editMode) {
            editVideo(video)
        } else {
            addVideo(video)
        }
        videoEditEvent(VideoEditEvent.Finish)
        videoListEvent(
            if (editMode) VideoListEvent.Edited else VideoListEvent.Added,
        )
    }

    private fun addVideo(
        video: YouTubeVideo,
    ) {
        val newVideo = video.copy(id = UUID.randomUUID().toString())
        _uiState.update {
            it.copy(
                videos = listOf(newVideo) + it.videos, // 최상단에 추가
                editingVideo = null,
            )
        }
    }

    private fun editVideo(
        video: YouTubeVideo,
    ) {
        _uiState.update {
            it.copy(
                videos = it.videos.map { old ->
                    if (old.id == video.id) video else old
                },
                editingVideo = null,
            )
        }
    }

    fun reorder(from: Int, to: Int) {
        val videos = uiState.value.videos.toMutableList()
        if (from !in videos.indices || to !in videos.indices) return

        _uiState.update {
            it.copy(
                videos = videos.apply { add(to, removeAt(from)) },
            )
        }
    }

    fun setEditMode() {
        _uiState.update { it.copy(editing = true) }
    }

    fun dismissExitAlertDialog() {
        _uiState.update { it.copy(showExitAlertDialog = false) }
    }

    private fun videoListEvent(event: VideoListEvent) {
        viewModelScope.launch {
            _videoListEvent.send(event)
        }
    }

    private fun videoEditEvent(event: VideoEditEvent) {
        viewModelScope.launch {
            _videoEditEvent.send(event)
        }
    }
}
