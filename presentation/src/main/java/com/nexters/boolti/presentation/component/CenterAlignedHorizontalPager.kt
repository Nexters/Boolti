package com.nexters.boolti.presentation.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.snapping.SnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import com.nexters.boolti.presentation.theme.BooltiTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CenterAlignedHorizontalPager(
    state: PagerState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues? = null,
    centerHorizontal: Boolean = true,
    pageSize: PageSize = PageSize.Fill,
    beyondBoundsPageCount: Int = PagerDefaults.BeyondBoundsPageCount,
    pageSpacing: Dp = 0.dp,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    flingBehavior: SnapFlingBehavior = PagerDefaults.flingBehavior(state = state),
    userScrollEnabled: Boolean = true,
    reverseLayout: Boolean = false,
    key: ((index: Int) -> Any)? = null,
    pageNestedScrollConnection: NestedScrollConnection = remember(state) {
        PagerDefaults.pageNestedScrollConnection(state, Orientation.Horizontal)
    },
    pageContent: @Composable PagerScope.(page: Int) -> Unit
) {
    var width by remember { mutableStateOf(0.dp) }
    val padding = contentPadding ?: PaddingValues(0.dp)
    val calculatedContentPadding = when (pageSize) {
        is PageSize.Fixed -> if (centerHorizontal) {
            val centerHorizontalPadding = (width - pageSize.pageSize).coerceAtLeast(0.dp) / 2
            PaddingValues(
                start = centerHorizontalPadding + padding.calculateStartPadding(LayoutDirection.Ltr),
                end = centerHorizontalPadding + padding.calculateEndPadding(LayoutDirection.Ltr),
                top = padding.calculateTopPadding(),
                bottom = padding.calculateBottomPadding(),
            )
        } else {
            padding
        }

        else -> padding
    }
    HorizontalPager(
        state = state,
        modifier = modifier.graphicsLayer {
            width = size.width.toDp()
        },
        contentPadding = calculatedContentPadding,
        pageSize = pageSize,
        beyondBoundsPageCount = beyondBoundsPageCount,
        pageSpacing = pageSpacing,
        verticalAlignment = verticalAlignment,
        flingBehavior = flingBehavior,
        userScrollEnabled = userScrollEnabled,
        reverseLayout = reverseLayout,
        key = key,
        pageNestedScrollConnection = pageNestedScrollConnection,
        pageContent = pageContent,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
private fun CenterAlignedHorizontalPagerPreview() {
    BooltiTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
        ) {
            val state = rememberPagerState { 5 }
            CenterAlignedHorizontalPager(
                modifier = Modifier.padding(vertical = 60.dp),
                state = state,
                pageSize = PageSize.Fixed(300.dp),
                pageSpacing = 20.dp,
            ) {
                Card(
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.outline),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    ),
                ) {
                    Box(
                        modifier = Modifier
                            .aspectRatio(0.75f),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = "Page #${state.currentPage + 1}")
                    }
                }
            }
        }
    }
}
