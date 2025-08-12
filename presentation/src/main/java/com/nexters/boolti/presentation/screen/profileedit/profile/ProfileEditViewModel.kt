package com.nexters.boolti.presentation.screen.profileedit.profile

import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.repository.UserConfigRepository
import com.nexters.boolti.domain.usecase.GetUserUsecase
import com.nexters.boolti.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ProfileEditViewModel @Inject constructor(
    private val userConfigRepository: UserConfigRepository,
    getUserUseCase: GetUserUsecase,
) : BaseViewModel() {
    private val _uiState = MutableStateFlow(ProfileEditState())
    val uiState = _uiState.asStateFlow()

    private val _event = Channel<ProfileEditEvent>()
    val event = _event.receiveAsFlow()

    init {
        getUserUseCase()?.let { user ->
            _uiState.update {
                it.copy(
                    id = user.userCode,
                    thumbnail = user.photo ?: "",
                    nickname = user.nickname,
                    introduction = user.introduction,
                    snsCount = user.sns.size,
                    videoCount = 0,
                    linkCount = user.link.totalSize,
                    upcomingShowCount = user.upcomingShow.totalSize,
                    pastShowCount = user.performedShow.totalSize,
                    showUpcomingShows = user.upcomingShow.isVisible == true,
                    showPerformedShows = user.performedShow.isVisible == true,
                )
            }
        } ?: event(ProfileEditEvent.UnAuthorized)
    }

    /*fun reorderSns(from: Int, to: Int) {
        val snsList = uiState.value.snsList.toMutableList()
        if (from !in snsList.indices || to !in snsList.indices) return

        _uiState.update {
            it.copy(
                snsList = snsList.apply { add(to, removeAt(from)) },
            )
        }
    }

    fun reorderLink(from: Int, to: Int) {
        val links = uiState.value.links.toMutableList()
        if (from !in links.indices || to !in links.indices) return

        _uiState.update {
            it.copy(
                links = links.apply { add(to, removeAt(from)) },
            )
        }
    }*/

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

    fun completeEdits(thumbnailFile: File?) {
        /*viewModelScope.launch(recordExceptionHandler) {
            _uiState.update { it.copy(saving = true) }

            val newThumbnailUrl = thumbnailFile?.let { file ->
                fileRepository.requestUrlForUpload(file).getOrNull()
            }

            authRepository.editProfile(
                EditProfileRequest(
                    nickname = uiState.value.nickname,
                    profileImagePath = newThumbnailUrl ?: uiState.value.thumbnail,
                    introduction = uiState.value.introduction,
                    sns = uiState.value.snsList.map { it.toDto() },
                    link = uiState.value.links.map { it.toDto() },
                )
            ).onSuccess {
                event(ProfileEditEvent.OnSuccessEditProfile)
            }.onFailure {
                event(ProfileEditEvent.EditFailed)
            }
            _uiState.update { it.copy(saving = false) }
        }*/
    }

    private fun event(event: ProfileEditEvent) {
        viewModelScope.launch {
            _event.send(event)
        }
    }
}
