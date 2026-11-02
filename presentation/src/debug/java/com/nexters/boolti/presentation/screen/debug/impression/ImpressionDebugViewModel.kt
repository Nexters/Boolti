package com.nexters.boolti.presentation.screen.debug.impression

import androidx.lifecycle.ViewModel
import com.nexters.boolti.common.tracker.AppTracker
import com.nexters.boolti.common.tracker.event.impression
import com.nexters.boolti.common.tracker.field.DebugImpression
import com.nexters.boolti.common.tracker.field.Item
import com.nexters.boolti.common.tracker.field.Role
import com.nexters.boolti.common.tracker.field.Screen
import com.nexters.boolti.common.tracker.impression.ImpressionEvent
import com.nexters.boolti.common.tracker.impression.ImpressionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

internal data class ImpressionDebugEntry(
    val number: Int,
    val key: Any?,
    val threshold: Float,
    val extras: Map<String, Any>,
)

internal data class ImpressionDebugUiState(
    val case: ImpressionDebugCase = ImpressionDebugCase.Basic,
    val threshold: Float = 0.5f,
    val session: Int = 1,
    val total: Int = 0,
    val counts: Map<String, Int> = emptyMap(),
    val entries: List<ImpressionDebugEntry> = emptyList(),
)

@HiltViewModel
internal class ImpressionDebugViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(ImpressionDebugUiState())
    val uiState = _uiState.asStateFlow()

    // UI에서 생성하는 상태와 달리 ViewModel 수명 동안 유지되는 예제임.
    var impressionState = ImpressionState(deduplicate = true, onImpressed = ::onImpressed)
        private set

    fun selectCase(case: ImpressionDebugCase) {
        if (case != _uiState.value.case) startSession(case = case, threshold = 0.5f)
    }

    fun selectThreshold(threshold: Float) {
        if (threshold != _uiState.value.threshold) startSession(threshold = threshold)
    }

    fun startSession(
        case: ImpressionDebugCase = _uiState.value.case,
        threshold: Float = _uiState.value.threshold,
    ) {
        impressionState = ImpressionState(deduplicate = true, onImpressed = ::onImpressed)
        _uiState.value = ImpressionDebugUiState(
            case = case,
            threshold = threshold,
            session = _uiState.value.session + 1,
        )
    }

    fun onImpressed(event: ImpressionEvent) {
        if (append(event.key, event.threshold, event.extras)) {
            AppTracker.impression(
                event = event,
                screen = Screen.DebugImpression,
                objectRole = Role.Item,
                objectValue = "ImpressionSample",
            )
        }
    }

    fun onLocalImpressed(extras: Map<String, Any>) {
        // 로컬 콜백에는 실제 자동 key가 전달되지 않으므로 임의의 key를 표시하지 않음.
        if (append(null, 0.5f, extras)) {
            AppTracker.impression(
                screen = Screen.DebugImpression,
                objectRole = Role.Item,
                objectValue = "ImpressionSample",
                properties = extras,
            )
        }
    }

    private fun append(key: Any?, threshold: Float, extras: Map<String, Any>): Boolean {
        if (extras["test_session"] != _uiState.value.session) return false
        val sample = extras.getValue("sample").toString()
        _uiState.update { current ->
            val number = current.total + 1
            current.copy(
                total = number,
                counts = current.counts + (sample to ((current.counts[sample] ?: 0) + 1)),
                entries = listOf(ImpressionDebugEntry(number, key, threshold, extras)) +
                    current.entries.take(29),
            )
        }
        return true
    }
}
