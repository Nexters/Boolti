package com.nexters.boolti.presentation.util

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class LogViewerState(
    val isVisible: Boolean = false,
    val isExpanded: Boolean = false,
    val offsetX: Dp = 0.dp,
    val offsetY: Dp = 100.dp,
)

object DebugManager {
    var logViewerState by mutableStateOf(LogViewerState())
        private set

    fun openLogViewer() {
        logViewerState = logViewerState.copy(
            isVisible = true,
            isExpanded = true,
        )
    }

    fun minimizeLogViewer() {
        logViewerState = logViewerState.copy(isExpanded = false)
    }

    fun closeLogViewer() {
        logViewerState = LogViewerState()
    }

    fun updatePosition(x: Dp, y: Dp) {
        logViewerState = logViewerState.copy(
            offsetX = x,
            offsetY = y,
        )
    }
}
