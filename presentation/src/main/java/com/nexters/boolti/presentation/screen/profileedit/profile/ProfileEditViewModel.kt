package com.nexters.boolti.presentation.screen.profileedit.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.repository.AuthRepository
import com.nexters.boolti.domain.repository.FileRepository
import com.nexters.boolti.domain.repository.UserConfigRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProfileEditViewModel @Inject constructor(
    private val userConfigRepository: UserConfigRepository,
    private val authRepository: AuthRepository,
    private val fileRepository: FileRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileEditUiState())
    val uiState: StateFlow<ProfileEditUiState> = _uiState.asStateFlow()

    private val _event = Channel<ProfileEditEvent>(Channel.BUFFERED)
    val event: Flow<ProfileEditEvent> = _event.receiveAsFlow()

    init {
        viewModelScope.launch {
            authRepository.cachedUser
                .filterNotNull()
                .collect { user ->
                    _uiState.update {
                        it.copy(
                            userCode = user.userCode,
                            thumbnail = user.photo.orEmpty(),
                            nickname = user.nickname,
                            introduction = user.introduction,
                            snsCount = user.sns.size,
                            videoCount = user.video.totalSize,
                            linkCount = user.link.totalSize,
                            upcomingShowCount = user.upcomingShow.totalSize,
                            pastShowCount = user.performedShow.totalSize,
                            showUpcomingShows = user.upcomingShow.isVisible == true,
                            showPerformedShows = user.performedShow.isVisible == true,
                        )
                    }
                }
        }
    }

    fun onAction(action: ProfileEditAction) {
        when (action) {
            ProfileEditAction.ClickBack -> _event.trySend(ProfileEditEvent.NavigateUp)
            is ProfileEditAction.SelectThumbnail -> changeThumbnail(action.imageUri)
            ProfileEditAction.ClickNickname -> _event.trySend(ProfileEditEvent.NavigateToNicknameEdit)
            ProfileEditAction.ClickUserCode -> _event.trySend(ProfileEditEvent.NavigateToUserCodeEdit)
            ProfileEditAction.ClickIntroduction -> _event.trySend(ProfileEditEvent.NavigateToIntroductionEdit)
            ProfileEditAction.ClickSns -> _event.trySend(ProfileEditEvent.NavigateToSnsEdit)
            ProfileEditAction.ToggleUpcomingShows -> toggleVisibility {
                userConfigRepository.setUpcomingShowVisible(!uiState.value.showUpcomingShows)
            }
            ProfileEditAction.TogglePastShows -> toggleVisibility {
                userConfigRepository.setPastShowVisible(!uiState.value.showPerformedShows)
            }
            ProfileEditAction.ClickVideo -> _event.trySend(ProfileEditEvent.NavigateToVideoEdit(uiState.value.userCode))
            ProfileEditAction.ClickLink -> _event.trySend(ProfileEditEvent.NavigateToLinkEdit(uiState.value.userCode))
        }
    }

    private fun changeThumbnail(imageUri: String) {
        if (uiState.value.uploading) return

        _uiState.update { it.copy(uploadingThumbnail = imageUri) }
        viewModelScope.launch {
            fileRepository.requestUrlForUpload(imageUri)
                .mapCatching { url -> userConfigRepository.saveThumbnail(url).getOrThrow() }
                .onFailure { notifyError(it) }
            _uiState.update { it.copy(uploadingThumbnail = null) }
        }
    }

    private fun toggleVisibility(request: suspend () -> Result<*>) {
        viewModelScope.launch {
            request().onFailure { notifyError(it) }
        }
    }

    private suspend fun notifyError(e: Throwable) {
        Timber.e(e)
        _event.send(ProfileEditEvent.ShowUnknownError)
    }
}
