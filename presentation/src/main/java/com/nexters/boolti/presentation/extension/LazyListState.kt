package com.nexters.boolti.presentation.extension

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged

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

/**
 * LazyList에서 하단에 도달했을 때 콜백을 실행하는 확장 함수
 *
 * @param buffer 하단으로부터 몇 개의 아이템 전에 콜백을 호출할지 (기본값: 3)
 * @param onLoadMore 하단에 도달했을 때 실행할 콜백 (다음 페이지 로드 등)
 */
@Composable
fun LazyListState.OnBottomReached(
    buffer: Int = 3,
    onLoadMore: () -> Unit
) {
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
                ?: return@derivedStateOf false

            val totalItemsCount = layoutInfo.totalItemsCount
            if (totalItemsCount == 0) return@derivedStateOf false

            lastVisibleItem.index >= totalItemsCount - 1 - buffer
        }
    }

    LaunchedEffect(shouldLoadMore) {
        snapshotFlow { shouldLoadMore.value }
            .distinctUntilChanged()
            .collect { isAtBottom ->
                if (isAtBottom) {
                    onLoadMore()
                }
            }
    }
}
