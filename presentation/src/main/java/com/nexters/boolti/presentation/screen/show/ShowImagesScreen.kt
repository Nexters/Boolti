package com.nexters.boolti.presentation.screen.show

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BtAppBar

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShowImagesScreen(
    index: Int,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ShowDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val pageState = rememberPagerState(
        initialPage = index, initialPageOffsetFraction = 0f
    ) { uiState.showDetail.images.size }

    Scaffold(
        topBar = {
            BtAppBar(
                title = "",
                onBackPressed = onBackPressed,
                navIconRes = R.drawable.ic_close,
            )
        },
        modifier = modifier,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(top = 56.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            HorizontalPager(
                modifier = Modifier,
                state = pageState,
                key = { it },
            ) {
                AsyncImage(
                    modifier = Modifier
                        .fillMaxWidth(),
                    model = uiState.showDetail.images[it].originImage,
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                )
            }
            Indicator(
                modifier = Modifier.padding(bottom = 20.dp),
                position = pageState.currentPage,
                size = pageState.pageCount,
            )
        }
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
                    .clip(shape = CircleShape)
                    .background(Color.White.copy(alpha = opacity))
            )
        }
    }
}