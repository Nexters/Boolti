package com.nexters.boolti.presentation.screen.report

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.nexters.boolti.presentation.screen.navigation.ShowRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val showId: String = checkNotNull(savedStateHandle.toRoute<ShowRoute.Report>().showId) {
        "showId가 전달되어야 합니다."
    }

    private val _uiState = MutableStateFlow(ReportUiState())
    val uiState: StateFlow<ReportUiState> = _uiState.asStateFlow()

    fun updateReason(newReason: String) {
        _uiState.update { it.copy(reason = newReason) }
    }
}
