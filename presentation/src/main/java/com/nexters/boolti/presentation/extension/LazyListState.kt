package com.nexters.boolti.presentation.extension

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember

@Composable
fun rememberHasScrolledState(lazyListState: LazyListState): State<Boolean> {
    return remember(lazyListState) {
        derivedStateOf {
            val firstIndex = lazyListState.firstVisibleItemIndex
            val firstOffset = lazyListState.firstVisibleItemScrollOffset
            firstIndex > 0 || firstOffset > 0
        }
    }
}

val LazyListState.hasScrolledState: State<Boolean>
    @Composable
    get() = rememberHasScrolledState(this)
