package com.nexters.boolti.presentation.screen.debug.info

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mangbaam.logger.LogCollector
import com.nexters.boolti.domain.repository.AuthRepository
import com.nexters.boolti.domain.repository.DebugReportRepository
import com.nexters.boolti.domain.repository.DeviceInfoRepository
import com.nexters.boolti.presentation.util.EXTRA_CURRENT_SCREEN
import com.nexters.boolti.presentation.util.EXTRA_SCREENSHOT_PATH
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

private const val LOG_LINES = 100

@HiltViewModel
internal class DebugInfoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    deviceInfoRepository: DeviceInfoRepository,
    authRepository: AuthRepository,
    private val debugReportRepository: DebugReportRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(DebugInfoUiState(canSend = debugReportRepository.canSend))
    val uiState: StateFlow<DebugInfoUiState> = _uiState.asStateFlow()

    private val _event = Channel<DebugInfoEvent>(Channel.BUFFERED)
    val event: Flow<DebugInfoEvent> = _event.receiveAsFlow()

    private val screenshotPath = savedStateHandle.get<String>(EXTRA_SCREENSHOT_PATH)

    // 팝업을 연 순간의 로그를 보낸다. 팝업이 떠 있는 동안 쌓인 로그는 제외한다
    private val logs = LogCollector.allLogs.value.takeLast(LOG_LINES)

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

    fun onAction(action: DebugInfoAction) {
        when (action) {
            DebugInfoAction.ClickSend -> send()
        }
    }

    private fun send() {
        if (_uiState.value.isSending) return
        _uiState.update { it.copy(isSending = true) }
        viewModelScope.launch {
            debugReportRepository.send(
                content = _uiState.value.text,
                logs = buildLogText(logs, ZoneId.systemDefault()),
                screenshotPath = screenshotPath,
            ).onSuccess {
                _event.send(DebugInfoEvent.SendSucceeded)
            }.onFailure { e ->
                Timber.w(e)
                _event.send(DebugInfoEvent.SendFailed)
            }
            _uiState.update { it.copy(isSending = false) }
        }
    }
}
