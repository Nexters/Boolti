package com.nexters.boolti.presentation.screen.ticket

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TicketScreen(
    onClickTicket: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val pagerState = rememberPagerState(
            pageCount = { 10 }
        )
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp

        val contentPadding = 30.dp
        val pageSpacing = 16.dp
        val scaleSizeRatio = 0.8f

        HorizontalPager(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
                .weight(1F),
            state = pagerState,
            contentPadding = PaddingValues(horizontal = contentPadding),
            pageSpacing = pageSpacing,
            beyondBoundsPageCount = 3,
        ) { page ->

            Card(
                Modifier
                    .fillMaxSize()
                    .padding(top = 36.dp, bottom = 16.dp)
                    .align(Alignment.CenterHorizontally)
                    .graphicsLayer {
                        val pageOffset =
                            ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction)

                        alpha = lerp(
                            start = 0.5f,
                            stop = 1f,
                            fraction = 1f - pageOffset.absoluteValue.coerceIn(0f, 1f),
                        )
                        lerp(
                            start = 1f,
                            stop = scaleSizeRatio,
                            fraction = pageOffset.absoluteValue.coerceIn(0f, 1f),
                        ).let {
                            scaleX = it
                            scaleY = it
                        }
                        translationX = calculateTranslationX(
                            screenWidth = screenWidth,
                            scaleRatio = scaleSizeRatio,
                            contentPadding = contentPadding,
                            pageOffset = pageOffset,
                        ).toPx()
                    },
            ) {
                TicketContent("https://images.khan.co.kr/article/2023/09/12/news-p.v1.20230912.69ec17ff44f14cc28a10fff6e935e41b_P1.png")
            }
        }

        Card(
            modifier = Modifier.padding(bottom = 28.dp),
            shape = RoundedCornerShape(100.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        ) {
            Text(
                text = "${pagerState.currentPage + 1}/${pagerState.pageCount}",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp),
            )
        }
    }
}

private fun calculateTranslationX(
    screenWidth: Int,
    scaleRatio: Float,
    contentPadding: Dp,
    pageOffset: Float,
): Dp {
    val pageFullWidth = screenWidth.dp - contentPadding
    return (((pageFullWidth * (1 - scaleRatio)) / 2) * pageOffset)
}
