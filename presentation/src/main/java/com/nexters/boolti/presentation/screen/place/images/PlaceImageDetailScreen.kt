package com.nexters.boolti.presentation.screen.place.images

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.nexters.boolti.domain.model.PlaceImage
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BtCircularProgressIndicator
import com.nexters.boolti.presentation.component.BtCloseableAppBar
import com.nexters.boolti.presentation.component.Indicator
import com.nexters.boolti.presentation.theme.BooltiTheme
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

/**
 * 공연장 사진 크게 보기 화면.
 *
 * 목록에서 선택한 사진부터 좌우로 넘겨 보며, 핀치/더블탭으로 확대할 수 있다.
 */
@Composable
fun PlaceImageDetailScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlaceImageDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PlaceImageDetailScreen(
        modifier = modifier,
        images = uiState.images,
        isLoading = uiState.isLoading,
        initialIndex = viewModel.initialIndex,
        onBack = onBack,
    )
}

@Composable
private fun PlaceImageDetailScreen(
    images: List<PlaceImage>,
    isLoading: Boolean,
    initialIndex: Int,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = { BtCloseableAppBar(onClickClose = onBack) },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            when {
                isLoading -> BtCircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                images.isNotEmpty() -> PlaceImagePager(
                    images = images,
                    initialIndex = initialIndex,
                )
            }
        }
    }
}

@Composable
private fun PlaceImagePager(
    images: List<PlaceImage>,
    initialIndex: Int,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(
        initialPage = initialIndex.coerceIn(images.indices),
        initialPageOffsetFraction = 0f,
    ) { images.size }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        HorizontalPager(
            modifier = Modifier.weight(1f),
            state = pagerState,
            key = { images[it].id },
        ) { page ->
            AsyncImage(
                modifier = Modifier
                    .fillMaxSize()
                    .zoomable(rememberZoomState()),
                model = images[page].imageUrl,
                contentDescription = stringResource(R.string.description_place_photo, page + 1),
                contentScale = ContentScale.Fit,
            )
        }

        Box(
            modifier = Modifier
                .navigationBarsPadding()
                .height(INDICATOR_AREA_HEIGHT),
            contentAlignment = Alignment.Center,
        ) {
            if (pagerState.pageCount > 1) {
                Indicator(
                    position = pagerState.currentPage,
                    size = pagerState.pageCount,
                )
            }
        }
    }
}

/** 인디케이터(7dp) 위아래 여백 20dp 포함. */
private val INDICATOR_AREA_HEIGHT = 47.dp

@Preview
@Composable
private fun PlaceImageDetailScreenPreview() {
    BooltiTheme {
        PlaceImageDetailScreen(
            images = List(5) { index ->
                PlaceImage(
                    id = index.toString(),
                    imageUrl = "",
                    thumbnailUrl = "",
                    sequence = index,
                )
            },
            isLoading = false,
            initialIndex = 0,
            onBack = {},
        )
    }
}
