package com.nexters.boolti.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * 다음 화면이 이 화면 [savedStateHandle]의 [key]에 넣어 돌려준 결과를 한 번만 전달한다.
 * NavBackStackEntry의 SavedStateHandle은 ViewModel이 받는 것과 다른 객체라서 화면에서 받아 넘긴다.
 */
@Composable
fun <T : Any> ObserveNavResult(
    savedStateHandle: SavedStateHandle,
    key: String,
    onResult: (T) -> Unit,
) {
    val result by savedStateHandle.getStateFlow<T?>(key, null).collectAsStateWithLifecycle()
    LaunchedEffect(result) {
        result?.let {
            savedStateHandle[key] = null
            onResult(it)
        }
    }
}
