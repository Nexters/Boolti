package com.nexters.boolti.presentation.screen.debug.info

internal data class DebugInfoUiState(
    val text: String = "",
    val canSend: Boolean = false,
    val isSending: Boolean = false,
)

internal sealed interface DebugInfoAction {
    data object ClickSend : DebugInfoAction
}

internal sealed interface DebugInfoEvent {
    data object SendSucceeded : DebugInfoEvent
    data object SendFailed : DebugInfoEvent
}
