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
    val showSettings: Boolean = false,
    val opacity: Float = 0.95f,
    val filterTags: Set<String> = emptySet(),
    val expandedLogIds: Set<String> = emptySet(),
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

    fun showSettings() {
        logViewerState = logViewerState.copy(showSettings = true)
    }

    fun hideSettings() {
        logViewerState = logViewerState.copy(showSettings = false)
    }

    fun updateOpacity(opacity: Float) {
        logViewerState = logViewerState.copy(opacity = opacity.coerceIn(0.3f, 1f))
    }

    fun updateFilterTags(tags: Set<String>) {
        logViewerState = logViewerState.copy(filterTags = tags)
    }

    fun addFilterTag(tag: String) {
        logViewerState = logViewerState.copy(
            filterTags = logViewerState.filterTags + tag
        )
    }

    fun removeFilterTag(tag: String) {
        logViewerState = logViewerState.copy(
            filterTags = logViewerState.filterTags - tag
        )
    }

    fun toggleLogExpanded(logId: String) {
        val expandedIds = logViewerState.expandedLogIds
        logViewerState = logViewerState.copy(
            expandedLogIds = if (expandedIds.contains(logId)) {
                expandedIds - logId
            } else {
                expandedIds + logId
            }
        )
    }
}
