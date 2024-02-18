package com.nexters.boolti.presentation.screen.show

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nexters.boolti.presentation.theme.Grey95

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SwipeableImage(
    models: List<String>,
    onImageClick: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pageState = rememberPagerState(
        initialPage = 0, initialPageOffsetFraction = 0f
    ) { models.size }

    Box(modifier = modifier, contentAlignment = Alignment.BottomCenter) {
        HorizontalPager(
            modifier = Modifier,
            state = pageState,
            key = { it },
        ) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(210f / 297f)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                    ) { onImageClick(it) },
                model = models[it],
                contentDescription = null,
                contentScale = ContentScale.Crop,
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clip(shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
                .background(
                    alpha = 0.5f,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent, Grey95
                        )
                    )
                )
        )
        Indicator(
            modifier = Modifier.padding(bottom = 14.dp),
            position = pageState.currentPage,
            size = models.size
        )
    }
}

@Composable
private fun Indicator(
    modifier: Modifier = Modifier,
    position: Int,
    size: Int,
) {
    Row(modifier = modifier) {
        (0 until size).forEach { index ->
            val opacity = if (index == position) 1f else 0.5f
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(7.dp)
                    .clip(shape = RoundedCornerShape(4.dp))
                    .background(Color.White.copy(alpha = opacity))
            )
        }
    }
}