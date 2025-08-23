package com.nexters.boolti.presentation.screen.profileedit.profile

import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.repository.AuthRepository
import com.nexters.boolti.domain.repository.UserConfigRepository
import com.nexters.boolti.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileEditViewModel @Inject constructor(
    private val userConfigRepository: UserConfigRepository,
    private val authRepository: AuthRepository,
) : BaseViewModel() {
    private val _uiState = MutableStateFlow(ProfileEditState())
    val uiState = _uiState.asStateFlow()

    private val _event = Channel<ProfileEditEvent>()
    val event = _event.receiveAsFlow()

    init {
        viewModelScope.launch {
            authRepository.cachedUser
                .filterNotNull()
                .collect { user ->
                    _uiState.update {
                        it.copy(
                            userCode = user.userCode,
                            thumbnail = user.photo ?: "",
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

    fun toggleShowUpcomingShows() {
        viewModelScope.launch {
            userConfigRepository.setUpcomingShowVisible(!uiState.value.showUpcomingShows)
                .onSuccess { result ->
                    _uiState.update { it.copy(showUpcomingShows = result.value) }
                }
        }
    }

    fun toggleShowPerformedShows() {
        viewModelScope.launch {
            userConfigRepository.setPastShowVisible(!uiState.value.showPerformedShows)
                .onSuccess { result ->
                    _uiState.update { it.copy(showPerformedShows = result.value) }
                }
        }
    }
}
