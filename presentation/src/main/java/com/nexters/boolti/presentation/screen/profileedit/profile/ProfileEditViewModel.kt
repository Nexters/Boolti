package com.nexters.boolti.presentation.screen.profileedit.profile

import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.model.Link
import com.nexters.boolti.domain.model.Sns
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
    val isDataChanged: Boolean
        get() = initialState != uiState.value

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
                snsList = user.sns,
                links = user.link,
            )
            _uiState.update {
                it.copy(
                    thumbnail = user.photo ?: "",
                    nickname = user.nickname,
                    introduction = user.introduction,
                    snsList = user.sns,
                    links = user.link,
                )
            }
        } ?: event(ProfileEditEvent.UnAuthorized)
    }

    fun changeNickname(nickname: String) {
        _uiState.update { it.copy(nickname = nickname) }
    }

    fun changeIntroduction(introduction: String) {
        _uiState.update { it.copy(introduction = introduction) }
    }

    fun onNewLinkAdded(newLink: Link) {
        _uiState.update { it.copy(links = it.links + listOf(newLink)) }
        event(ProfileEditEvent.OnLinkAdded)
    }

    fun onLinkEdited(link: Link) {
        val newLinks = uiState.value.links.toMutableList().apply {
            val index = indexOfFirst { it.id == link.id }
            set(index, link)
        }
        _uiState.update { it.copy(links = newLinks) }
        event(ProfileEditEvent.OnLinkEdited)
    }

    fun onLinkRemoved(id: String) {
        val newLinks = uiState.value.links.toMutableList().apply {
            removeIf { it.id == id }
        }
        _uiState.update { it.copy(links = newLinks) }
        event(ProfileEditEvent.OnLinkRemoved)
    }

    fun onSnsAdded(sns: Sns) {
        _uiState.update { it.copy(snsList = it.snsList + listOf(sns)) }
        event(ProfileEditEvent.OnSnsAdded)
    }

    fun onSnsEdited(sns: Sns) {
        val newSnsList = uiState.value.snsList.toMutableList().apply {
            val index = indexOfFirst { it.id == sns.id }
            set(index, sns)
        }
        _uiState.update { it.copy(snsList = newSnsList) }
        event(ProfileEditEvent.OnSnsEdited)
    }

    fun onSnsRemoved(id: String) {
        val newSnsList = uiState.value.snsList.toMutableList().apply {
            removeIf { it.id == id }
        }
        _uiState.update { it.copy(snsList = newSnsList) }
        event(ProfileEditEvent.OnSnsRemoved)
    }

    fun reorderSns(from: Int, to: Int) {
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
    }

    fun completeEdits(thumbnailFile: File?) {
        viewModelScope.launch(recordExceptionHandler) {
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
        }
    }

    private fun event(event: ProfileEditEvent) {
        viewModelScope.launch {
            _event.send(event)
        }
    }
}
