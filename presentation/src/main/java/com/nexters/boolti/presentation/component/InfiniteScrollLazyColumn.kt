package com.nexters.boolti.presentation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nexters.boolti.presentation.extension.OnBottomReached

/**
 * 무한 스크롤 기능이 내장된 LazyColumn
 *
 * @param isLoading 현재 페이지를 로드 중인지 여부
 * @param onBottomReached 리스트 하단에 도달했을 때 호출될 콜백
 * @param modifier Modifier
 * @param state LazyListState
 * @param contentPadding LazyColumn의 contentPadding
 * @param verticalArrangement LazyColumn의 verticalArrangement
 * @param loadingIndicatorPadding 로딩 인디케이터의 padding
 * @param content LazyColumn의 content (LazyListScope를 사용)
 */
@Composable
fun InfiniteScrollLazyColumn(
    isLoading: Boolean,
    onBottomReached: () -> Unit,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    loadingIndicatorPadding: Dp = 16.dp,
    content: LazyListScope.() -> Unit,
) {
    state.OnBottomReached {
        onBottomReached()
    }

    LazyColumn(
        state = state,
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement,
    ) {
        content()

        item(key = "loading_indicator") {
            AnimatedVisibility(
                visible = isLoading,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = loadingIndicatorPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    BtCircularProgressIndicator()
                }
            }
        }
    }
}
