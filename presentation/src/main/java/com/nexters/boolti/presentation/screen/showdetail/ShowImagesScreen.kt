package com.nexters.boolti.presentation.screen.showdetail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.nexters.boolti.presentation.component.BtCloseableAppBar
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

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
        topBar = { BtCloseableAppBar(onClickClose = onBackPressed) },
        modifier = modifier,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                HorizontalPager(
                    state = pageState,
                    key = { it },
                ) {
                    AsyncImage(
                        modifier = Modifier
                            .fillMaxSize()
                            .zoomable(rememberZoomState()),
                        model = uiState.showDetail.images[it].originImage,
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                    )
                }
            }

            Box(
                modifier = Modifier.height(47.dp), // indicator 높이가 7이라...
                contentAlignment = Alignment.Center
            ) {
                if (pageState.pageCount > 1) {
                    Indicator(
                        position = pageState.currentPage,
                        size = pageState.pageCount,
                    )
                }
            }
        }
    }
}
