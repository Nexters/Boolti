package com.nexters.boolti.presentation.screen.gift

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class GiftViewModel @Inject constructor(

) : ViewModel() {
    private val _uiState = MutableStateFlow(GiftUiState())
    val uiState: StateFlow<GiftUiState> = _uiState.asStateFlow()

    fun toggleAgreement() {
        _uiState.update { it.toggleAgreement() }
    }
}
