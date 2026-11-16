package com.nexters.boolti.presentation.screen.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.nexters.boolti.domain.model.User
import com.nexters.boolti.domain.repository.AuthRepository
import com.nexters.boolti.domain.repository.MemberRepository
import com.nexters.boolti.domain.repository.YouTubeRepository
import com.nexters.boolti.presentation.base.BaseViewModel
import com.nexters.boolti.presentation.screen.navigation.MainRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val memberRepository: MemberRepository,
    private val authRepository: AuthRepository,
    private val youTubeRepository: YouTubeRepository,
) : BaseViewModel() {
    private val route = savedStateHandle.toRoute<MainRoute.Profile>()
    private val _userCode: String? = route.userCode
    val source: String = route.source
    private var previousVideoIds: List<String>? = null

    private val _uiState = MutableStateFlow(
        ProfileState(
            isMine = _userCode == null,
            user = if (_userCode == null) User.My("") else User.Others(),
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _event = Channel<ProfileEvent>()
    val event = _event.receiveAsFlow()

    init {
        viewModelScope.launch(recordExceptionHandler) {
            val othersUserCode = _userCode?.takeIf { it != authRepository.getUser().onFailure { Timber.e(it) }.getOrNull()?.userCode }
            if (othersUserCode == null) {
                _uiState.update { it.copy(isMine = true) }
                collectMyProfile()
            } else {
                fetchOthersProfile(othersUserCode)
            }
        }
    }

    private fun event(event: ProfileEvent) {
        viewModelScope.launch {
            _event.send(event)
        }
    }

    private fun collectMyProfile() {
        authRepository.cachedUser
            .filterNotNull()
            .onEach { user ->
                _uiState.update { it.copy(user = user) }

                val currentVideoIds = user.video.previewItems
                if (currentVideoIds != previousVideoIds) {
                    loadYouTubeVideos(currentVideoIds)
                    previousVideoIds = currentVideoIds
                }
            }
            .launchIn(viewModelScope)
    }

    private fun fetchOthersProfile(userCode: String) {
        viewModelScope.launch(recordExceptionHandler) {
            memberRepository.getMember(userCode).onSuccess { user ->
                _uiState.update { it.copy(user = user) }
                loadYouTubeVideos(user.video.previewItems)
            }.onFailure {
                event(ProfileEvent.Invalid)
            }
        }
    }

    private fun loadYouTubeVideos(videoUrls: List<String>) {
        if (videoUrls.isEmpty()) {
            _uiState.update { it.copy(youtubeVideos = emptyList()) }
            return
        }

        viewModelScope.launch(recordExceptionHandler) {
            _uiState.update { it.copy(loading = true) }

            val youtubeVideos = youTubeRepository.getVideoInfoByUrlList(videoUrls)

            _uiState.update {
                it.copy(
                    youtubeVideos = youtubeVideos,
                    loading = false
                )
            }
        }
    }
}
