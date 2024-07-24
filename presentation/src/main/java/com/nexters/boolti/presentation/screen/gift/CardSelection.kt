package com.nexters.boolti.presentation.screen.gift

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nexters.boolti.domain.model.ImagePair
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.Grey10


@Composable
fun CardSelection(
    message: String,
    onMessageChanged: (String) -> Unit,
    images: List<ImagePair>,
    selectedImage: ImagePair?,
    onImageSelected: (ImagePair) -> Unit,
) {
    Column(
        modifier = Modifier.padding(top = 24.dp, bottom = 48.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFF5A14),
                            Color(0xFFFFA883),
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    color = Color(0xFFFFA883),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 20.dp, vertical = 32.dp)
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

            AsyncImage(
                model = selectedImage?.originImage,
                contentDescription = stringResource(id = R.string.gift_selected_image),
                modifier = Modifier
                    .padding(top = 28.dp)
                    .fillMaxWidth()
                    .aspectRatio(3 / 2f)
                    .background(Color.White),
                contentScale = ContentScale.Crop,
            )
        }

        // TODO: 현재 선택 가능한 카드가 1개, 이후 카드 개수가 추가되면 주석 풀기!
//        CardCarousel(
//            modifier = Modifier
//                .padding(top = 44.dp)
//                .fillMaxWidth(),
//            images = images,
//            selectedImage = selectedImage,
//            onImageSelected = onImageSelected,
//        )
    }
}

@Composable
private fun CardCarousel(
    images: List<ImagePair>,
    selectedImage: ImagePair?,
    onImageSelected: (ImagePair) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(start = 32.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(images) { image ->
            val cardModifier = if (image == selectedImage) {
                Modifier.border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(4.dp)
                )
            } else {
                Modifier
            }

            AsyncImage(
                model = image.thumbnailImage,
                contentDescription = stringResource(id = R.string.gift_image),
                modifier = cardModifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .clickable {
                        onImageSelected(image)
                    },
                contentScale = ContentScale.Crop,
            )
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
            },
            selectedImage = ImagePair("", "", ""),
            onImageSelected = {}
        )
    }
}
