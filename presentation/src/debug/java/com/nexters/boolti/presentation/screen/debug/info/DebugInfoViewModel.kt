package com.nexters.boolti.presentation.screen.debug.info

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexters.boolti.domain.repository.AuthRepository
import com.nexters.boolti.domain.repository.DeviceInfoRepository
import com.nexters.boolti.presentation.util.EXTRA_CURRENT_SCREEN
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
internal class DebugInfoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    deviceInfoRepository: DeviceInfoRepository,
    authRepository: AuthRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(DebugInfoUiState())
    val uiState: StateFlow<DebugInfoUiState> = _uiState.asStateFlow()

    init {
        // 팝업을 연 순간의 상태를 한 번만 담는다
        val deviceInfo = deviceInfoRepository.getDeviceInfo()
        val currentScreen = savedStateHandle.get<String>(EXTRA_CURRENT_SCREEN)
        viewModelScope.launch {
            val text = buildDebugInfoText(
                deviceInfo = deviceInfo,
                user = authRepository.cachedUser.first(),
                currentScreen = currentScreen,
                now = LocalDateTime.now(),
            )
            _uiState.update { it.copy(text = text) }
        }
    }
}
