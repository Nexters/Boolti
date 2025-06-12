package com.nexters.boolti.presentation.screen.profileedit.sns

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.nexters.boolti.domain.model.Sns
import com.nexters.boolti.domain.usecase.GetUserUsecase
import com.nexters.boolti.presentation.base.BaseViewModel
import com.nexters.boolti.presentation.screen.navigation.ProfileRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SnsEditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getUserUsecase: GetUserUsecase,
) : BaseViewModel() {
    private val route = savedStateHandle.toRoute<ProfileRoute.ProfileSnsEdit>()
    private val my = getUserUsecase()

    private val _uiState = MutableStateFlow(
        SnsEditState(
            snsId = route.linkId,
            selectedSns = route.snsType,
            username = route.username ?: "",
            inUseSnsTypes = my?.sns?.map { it.type } ?: emptyList(),
        )
    )
    val uiState = _uiState.asStateFlow()

    fun setUsername(username: String) {
        _uiState.update {
            it.copy(username = username)
        }
    }

    fun setSns(sns: Sns.SnsType) {
        _uiState.update { it.copy(selectedSns = sns) }
    }
}
