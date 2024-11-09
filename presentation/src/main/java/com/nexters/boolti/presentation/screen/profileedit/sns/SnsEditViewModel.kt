package com.nexters.boolti.presentation.screen.profileedit.sns

import androidx.lifecycle.SavedStateHandle
import com.nexters.boolti.domain.model.Sns
import com.nexters.boolti.presentation.base.BaseViewModel
import com.nexters.boolti.presentation.screen.linkId
import com.nexters.boolti.presentation.screen.snsType
import com.nexters.boolti.presentation.screen.username
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SnsEditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : BaseViewModel() {
    private val snsId: String? = savedStateHandle[linkId]
    private val _uiState = MutableStateFlow(
        SnsEditState(
            selectedSns = Sns.SnsType.fromString(savedStateHandle[snsType])
                ?: Sns.SnsType.INSTAGRAM,
            username = savedStateHandle[username] ?: "",
            isEditMode = snsId != null,
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
