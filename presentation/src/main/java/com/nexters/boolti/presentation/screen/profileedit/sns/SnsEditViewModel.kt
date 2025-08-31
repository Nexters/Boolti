package com.nexters.boolti.presentation.screen.profileedit.sns

import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.model.Sns
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
import javax.inject.Inject

@HiltViewModel
class SnsEditViewModel @Inject constructor(
    getUserUsecase: GetUserUsecase,
    private val userConfigRepository: UserConfigRepository,
) : BaseViewModel() {
    private val snsList: Map<Sns.SnsType, String>? =
        getUserUsecase()?.sns?.associate { it.type to it.username }
    private val originalInstagramUsername: String = snsList?.get(Sns.SnsType.INSTAGRAM) ?: ""
    private val originalYoutubeUsername: String = snsList?.get(Sns.SnsType.YOUTUBE) ?: ""

    private val _uiState = MutableStateFlow(
        SnsEditState(
            originalInstagramUsername = originalInstagramUsername,
            originalYoutubeUsername = originalYoutubeUsername,
            instagramUsername = originalInstagramUsername,
            youtubeUsername = originalYoutubeUsername,
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _event = Channel<SnsEditEvent>()
    val event = _event.receiveAsFlow()

    fun changeInstagramUsername(username: String) {
        _uiState.update { it.copy(instagramUsername = username) }
    }

    fun changeYoutubeUsername(username: String) {
        _uiState.update { it.copy(youtubeUsername = username) }
    }

    fun saveSns() {
        if (uiState.value.saving) return

        _uiState.update { it.copy(saving = true) }
        viewModelScope.launch {
            userConfigRepository.saveSns(
                listOf(
                    Sns(
                        id = "",
                        type = Sns.SnsType.INSTAGRAM,
                        username = uiState.value.instagramUsername,
                    ),
                    Sns(
                        id = "",
                        type = Sns.SnsType.YOUTUBE,
                        username = uiState.value.youtubeUsername,
                    ),
                )
            )
                .onSuccess {
                    _uiState.update { it.copy(saving = false) }
                    event(SnsEditEvent.Saved)
                }
                .onFailure {
                    _uiState.update { it.copy(saving = false) }
                }
        }
    }

    fun showExitAlertDialog() {
        _uiState.update { it.copy(showExitAlertDialog = true) }
    }

    fun dismissExitAlertDialog() {
        _uiState.update { it.copy(showExitAlertDialog = false) }
    }

    fun checkCanExit(): Boolean {
        val state = uiState.value
        val canExit = (state.instagramUsername == originalInstagramUsername &&
                state.youtubeUsername == originalYoutubeUsername) ||
                (state.instagramUsernameError != null &&
                        state.youtubeUsernameError != null)
        if (!canExit) showExitAlertDialog()
        return canExit
    }

    private fun event(event: SnsEditEvent) {
        viewModelScope.launch {
            _event.send(event)
        }
    }
}
