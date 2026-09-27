package com.nexters.boolti.presentation.screen.place.images

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.nexters.boolti.domain.model.PlaceImage
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BtBackAppBar
import com.nexters.boolti.presentation.component.BtCircularProgressIndicator
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.util.ObserveAsEvents

/**
 * 공연장 사진 목록 화면.
 */
@Composable
fun PlaceImagesScreen(
    onBack: () -> Unit,
    onClickImage: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlaceImagesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) {
        when (it) {
            is PlaceImagesEvent.NavigateToDetail -> {
                onClickImage(it.index)
            }
        }
    }

    PlaceImagesScreen(
        modifier = modifier,
        images = uiState.images,
        isLoading = uiState.isLoading,
        onBack = onBack,
        onClickImage = onClickImage,
    )
}

@Composable
private fun PlaceImagesScreen(
    images: List<PlaceImage>,
    isLoading: Boolean,
    onBack: () -> Unit,
    onClickImage: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            BtBackAppBar(
                title = stringResource(R.string.place_photos),
                onClickBack = onBack,
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            if (isLoading) {
                BtCircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyVerticalGrid(
                    modifier = Modifier
                        .fillMaxSize()
                        .navigationBarsPadding(),
                    columns = AdaptiveWithMaxCount(minSize = 118.dp, maxCount = 6),
                    horizontalArrangement = Arrangement.spacedBy(GRID_SPACING),
                    verticalArrangement = Arrangement.spacedBy(GRID_SPACING),
                ) {
                    itemsIndexed(
                        items = images,
                        key = { _, image -> image.id },
                    ) { index, image ->
                        PlaceImageThumbnail(
                            image = image,
                            position = index + 1,
                            onClick = { onClickImage(index) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaceImageThumbnail(
    image: PlaceImage,
    position: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick),
        model = image.thumbnailUrl,
        contentDescription = stringResource(R.string.description_place_photo, position),
        contentScale = ContentScale.Crop,
    )
}

private val GRID_SPACING = 1.dp

/**
 * [GridCells.Adaptive]와 동일하게 [minSize] 기준으로 열 개수를 정하되, 최대 [maxCount]개로 제한한다.
 */
private class AdaptiveWithMaxCount(
    private val minSize: Dp,
    private val maxCount: Int,
) : GridCells {
    override fun Density.calculateCrossAxisCellSizes(availableSize: Int, spacing: Int): List<Int> {
        val count = ((availableSize + spacing) / (minSize.roundToPx() + spacing))
            .coerceIn(1, maxCount)
        val sizeWithoutSpacing = availableSize - spacing * (count - 1)
        val cellSize = sizeWithoutSpacing / count
        val remainder = sizeWithoutSpacing % count
        return List(count) { cellSize + if (it < remainder) 1 else 0 }
    }

    override fun equals(other: Any?): Boolean =
        other is AdaptiveWithMaxCount && minSize == other.minSize && maxCount == other.maxCount

    override fun hashCode(): Int = 31 * minSize.hashCode() + maxCount
}

@Preview
@Composable
private fun PlaceImagesScreenPreview() {
    BooltiTheme {
        PlaceImagesScreen(
            images = List(7) { index ->
                PlaceImage(
                    id = index.toString(),
                    imageUrl = "",
                    thumbnailUrl = "",
                    sequence = index,
                )
            },
            isLoading = false,
            onBack = {},
            onClickImage = {},
        )
    }
}

@Preview
@Composable
private fun PlaceImagesScreenLoadingPreview() {
    BooltiTheme {
        PlaceImagesScreen(
            images = emptyList(),
            isLoading = true,
            onBack = {},
            onClickImage = {},
        )
    }
}
