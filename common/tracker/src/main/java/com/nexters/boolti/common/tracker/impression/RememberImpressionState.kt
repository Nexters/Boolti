package com.nexters.boolti.common.tracker.impression

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable

/** 화면의 노출 이력을 저장 복원하며, 복원된 상태에도 최신 콜백을 연결함. */
@Composable
fun rememberImpressionState(
    deduplicate: Boolean = false,
    onImpressed: (ImpressionEvent) -> Unit = {},
): ImpressionState {
    val currentCallback = rememberUpdatedState(onImpressed)
    val state = rememberSaveable(deduplicate, saver = ImpressionState.Saver) {
        ImpressionState(deduplicate) { currentCallback.value(it) }
    }
    SideEffect { state.onImpressed = { currentCallback.value(it) } }
    return state
}
