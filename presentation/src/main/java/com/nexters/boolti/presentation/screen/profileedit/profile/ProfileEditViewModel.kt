package com.nexters.boolti.presentation.screen.profileedit.profile

import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.model.Link
import com.nexters.boolti.domain.repository.AuthRepository
import com.nexters.boolti.domain.repository.FileRepository
import com.nexters.boolti.domain.request.EditProfileRequest
import com.nexters.boolti.domain.request.toDto
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
    private val authRepository: AuthRepository,
    private val fileRepository: FileRepository,
    getUserUseCase: GetUserUsecase,
) : BaseViewModel() {
    private var initialState = ProfileEditState()

    private val _uiState = MutableStateFlow(ProfileEditState())
    val uiState = _uiState.asStateFlow()

    private val _event = Channel<ProfileEditEvent>()
    val event = _event.receiveAsFlow()

    init {
        getUserUseCase()?.let { user ->
            initialState = ProfileEditState(
                thumbnail = user.photo ?: "",
                nickname = user.nickname,
                introduction = user.introduction,
                links = user.link,
            )
            _uiState.update {
                it.copy(
                    thumbnail = user.photo ?: "",
                    nickname = user.nickname,
                    introduction = user.introduction,
                    links = user.link,
                )
            }
        }
    }

    fun changeNickname(nickname: String) {
        _uiState.update { it.copy(nickname = nickname) }
    }

    fun changeIntroduction(introduction: String) {
        _uiState.update { it.copy(introduction = introduction) }
    }

    fun onNewLinkAdded(newLink: Link) {
        _uiState.update { it.copy(links = listOf(newLink) + it.links) }
        event(ProfileEditEvent.OnLinkAdded)
    }

    fun onLinkEditted(link: Link) {
        val newLinks = uiState.value.links.toMutableList().apply {
            val index = indexOfFirst { it.id == link.id }
            set(index, link)
        }
        _uiState.update { it.copy(links = newLinks) }
        event(ProfileEditEvent.OnLinkEditted)
    }

    fun onLinkRemoved(id: String) {
        val newLinks = uiState.value.links.toMutableList().apply {
            removeIf { it.id == id }
        }
        _uiState.update { it.copy(links = newLinks) }
        event(ProfileEditEvent.OnLinkRemoved)
    }

    fun completeEdits(thumbnailRequestBody: File?) {
        viewModelScope.launch(recordExceptionHandler) {
            /*val uploadUrl = thumbnailRequestBody?.let {
                fileRepository.requestUrlForUpload(thumbnailRequestBody)
            }?.getOrNull()*/
            authRepository.editProfile(
                EditProfileRequest(
                    nickname = uiState.value.nickname,
                    profileImagePath = "", // uploadUrl ?: "",
                    introduction = uiState.value.introduction,
                    link = uiState.value.links.map { it.toDto() },
                )
            ).onSuccess {
                event(ProfileEditEvent.OnSuccessEditProfile)
            }
        }
    }

    private fun event(event: ProfileEditEvent) {
        viewModelScope.launch {
            _event.send(event)
        }
    }
}
