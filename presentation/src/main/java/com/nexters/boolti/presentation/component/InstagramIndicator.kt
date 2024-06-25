package com.nexters.boolti.presentation.component

import android.animation.ArgbEvaluator
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nexters.boolti.presentation.theme.BooltiTheme
import kotlin.math.absoluteValue

private data class IndicatorRange(
    @androidx.annotation.IntRange(from = 0) val start: Int,
    @androidx.annotation.IntRange(from = 0) val end: Int,
) {
    init {
        check(start <= end)
    }

    val size: Int
        get() = (end - start).absoluteValue + 1

    operator fun invoke(): IntRange = start..end
    operator fun minus(amount: Int): IndicatorRange = copy(start = start - amount, end = end - amount)
    operator fun plus(amount: Int): IndicatorRange = copy(start = start + amount, end = end + amount)
}

private class IndicatorUtil(
    val dotCount: Int = 5,
    val dotSize: Dp = 7.dp,
    val mediumDotSize: Dp = 5.dp,
    val smallDotSize: Dp = 4.dp,
    val spacedBy: Dp = 8.dp,
    val activeColor: Color = White,
    val inactiveColor: Color = White.copy(alpha = 0.5f),
) {
    private val evaluator = ArgbEvaluator()

    fun calculateWidth(pageCount: Int): Dp {
        if (pageCount == 0) return 0.dp

        val visibleCount = minOf(pageCount, dotCount) + 4
        return dotSize * visibleCount + spacedBy * (visibleCount - 1)
    }

    fun calculateHeight(): Dp = maxOf(dotSize, mediumDotSize, smallDotSize)

    fun calculateDotSize(
        index: Int,
        range: IndicatorRange,
        offsetFraction: Float,
    ): Dp {
        if (index in range()) return dotSize / 2

        val diff = when (index < range.start) {
            true -> offsetFraction - index
            false -> index - (offsetFraction + dotCount - 1)
        }

        return when {
            diff < 1f -> (dotSize + (mediumDotSize - dotSize) * diff) / 2
            diff < 2f -> (mediumDotSize + (smallDotSize - mediumDotSize) * (diff - 1f)) / 2
            diff < 3f -> (smallDotSize - mediumDotSize * (diff - 2f)) / 2
            else -> 0.dp
        }
    }

    fun calculateDotColor(index: Int, pageCount: Int, pageFraction: Float): Color = when {
        index in 0 until pageCount -> {
            val fraction = 1 - (index - pageFraction).absoluteValue.coerceAtMost(1f)
            Color(evaluator.evaluate(fraction, inactiveColor.toArgb(), activeColor.toArgb()) as Int)
        }

        else -> Color.Transparent
    }

    fun calculateX(
        index: Int,
        offsetFraction: Float,
    ): Dp = dotSize / 2 + (dotSize + spacedBy) * index +
            (dotSize + spacedBy) * 2 -
            (dotSize + spacedBy) * offsetFraction
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InstagramIndicator(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    dotCount: Int = 6,
    dotSize: Dp = 7.dp,
    spacedBy: Dp = 8.dp,
    activeColor: Color = White,
    inactiveColor: Color = White.copy(alpha = 0.5f),
) {
    val indicatorUtil = remember {
        IndicatorUtil(
            dotCount = dotCount,
            dotSize = dotSize,
            mediumDotSize = dotSize * 0.714f,
            smallDotSize = dotSize * 0.571f,
            spacedBy = spacedBy,
            activeColor = activeColor,
            inactiveColor = inactiveColor,
        )
    }

    /**
     * Normal 크기의 Dot 범위
     *
     * 이 범위 내에서는 인디케이터가 슬라이딩 되지 않고 active dot 을 이동할 수 있다.
     */
    var range by remember {
        val s = when {
            pagerState.currentPage >= dotCount -> (pagerState.currentPage - dotCount)
            else -> 0
        }
        mutableStateOf(
            IndicatorRange(
                start = s,
                end = s + dotCount - 1,
            )
        )
    }

    val pageFraction by remember {
        derivedStateOf {
            pagerState.currentPage + pagerState.currentPageOffsetFraction
        }
    }

    /**
     * 화면에 보이는 모든 Dot 범위
     *
     * [range] 양 옆에 2개의 추가 Dot 이 있으며, 표시할 수 있는 페이지 범위에서 벗어난 경우 화면에 그리지 않는다.
     */
    val visibleRange by remember {
        derivedStateOf {
            IndicatorRange(range.start - 2, range.end + 2)
        }
    }

    val offsetFraction by animateFloatAsState(
        targetValue = range.start.toFloat(),
        animationSpec = spring(),
        label = "offsetFraction",
    )

    /**
     * 현재 페이지가 [range]를 벗어난 경우 [range]를 조정
     */
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            range = when {
                page > range.end -> range + 1
                page < range.start -> range - 1
                else -> range
            }
        }
    }

    val indicatorWidth = remember(pagerState.pageCount) {
        indicatorUtil.calculateWidth(pagerState.pageCount)
    }

    val indicatorHeight = remember(indicatorUtil) {
        indicatorUtil.calculateHeight()
    }

    Canvas(
        modifier = modifier
            .size(width = indicatorWidth, height = indicatorHeight),
    ) {
        repeat(visibleRange.size) { i ->
            val index = visibleRange.start + i
            drawCircle(
                color = indicatorUtil.calculateDotColor(index, pagerState.pageCount, pageFraction),
                radius = indicatorUtil.calculateDotSize(index, range, offsetFraction).toPx(),
                center = Offset(
                    x = indicatorUtil.calculateX(index, offsetFraction).toPx(),
                    y = center.y,
                ),
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
private fun InstagramIndicatorPreview() {
    BooltiTheme {
        Surface {
            val pagerState = rememberPagerState { 20 }

            Column(
                modifier = Modifier.padding(vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                HorizontalPager(
                    modifier = Modifier
                        .height(400.dp),
                    state = pagerState,
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    pageSpacing = 16.dp,
                ) { page ->
                    Card(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("page: ${page + 1}")
                        }
                    }
                }
                InstagramIndicator(
                    pagerState = pagerState,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}
