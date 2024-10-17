package com.nexters.boolti.presentation.screen.gift

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.request.ImageRequest
import com.nexters.boolti.domain.model.ImagePair
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.Grey10
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList


@Composable
fun CardSelection(
    message: String,
    onMessageChanged: (String) -> Unit,
    images: ImmutableList<ImagePair>,
    selectedImage: ImagePair?,
    onImageSelected: (ImagePair) -> Unit,
) {
    var showBorder by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(top = 24.dp, bottom = 48.dp),
    ) {
        // 이미지 영역
        Box(
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(
                    width = if (showBorder) 1.dp else 0.dp,
                    color = Color.White.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(8.dp)
                )
        ) {
            AsyncImage(
                modifier = Modifier.aspectRatio(311 / 394f), // 선물 이미지 사이즈
                model = ImageRequest.Builder(LocalContext.current)
                    .data(selectedImage?.originImage)
                    .crossfade(true)
                    .build(),
                contentDescription = stringResource(id = R.string.gift_selected_image),
                contentScale = ContentScale.Crop,
                onState = { state ->
                    showBorder = state is AsyncImagePainter.State.Success
                }
            )

            Column(
                modifier = Modifier
                    .padding(top = 32.dp)
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val maximumLength = 40
                val messageLengthUnit = stringResource(id = R.string.gift_message_length_unit)

                BasicTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    value = message,
                    onValueChange = onMessageChanged,
                    textStyle = MaterialTheme.typography.titleLarge.copy(
                        color = Color.White,
                        textAlign = TextAlign.Center
                    ),
                    cursorBrush = SolidColor(Color.White)
                )
                Text(
                    modifier = Modifier.padding(top = 12.dp),
                    text = "${message.length}/${maximumLength}${messageLengthUnit}",
                    style = MaterialTheme.typography.labelMedium.copy(color = Grey10),
                )
            }
        }

        CardCarousel(
            modifier = Modifier
                .padding(top = 32.dp)
                .fillMaxWidth(),
            images = images,
            selectedImage = selectedImage,
            onImageSelected = onImageSelected,
        )
    }
}

@Composable
private fun CardCarousel(
    images: ImmutableList<ImagePair>,
    selectedImage: ImagePair?,
    onImageSelected: (ImagePair) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 32.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(images) { image ->
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .clickable {
                        onImageSelected(image)
                    }
            ) {
                AsyncImage(
                    model = image.thumbnailImage,
                    contentDescription = stringResource(id = R.string.gift_image),
                    contentScale = ContentScale.Crop,
                )

                if (image == selectedImage) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .background(
                                color = Color.Black.copy(alpha = 0.45f),
                                shape = RoundedCornerShape(4.dp)
                            )
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun CardSelectionPreview() {
    BooltiTheme {
        CardSelection(
            message = "공연에 초대합니다.",
            onMessageChanged = {},
            images = (1..10).map {
                ImagePair(
                    it.toString(),
                    "https://picsum.photos/200",
                    "https://picsum.photos/200"
                )
            }.toPersistentList(),
            selectedImage = ImagePair("", "", ""),
            onImageSelected = {}
        )
    }
}
