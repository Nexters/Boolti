package com.nexters.boolti.presentation.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey80
import com.nexters.boolti.presentation.theme.aggroFamily

@Composable
fun Show(
    modifier: Modifier = Modifier,
) {
    val borderRadius = 8.dp
    Column(
        modifier = modifier,
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("https://picsum.photos/200/300")
                .build(),
            contentDescription = "poster",
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(borderRadius))
                .border(
                    width = 1.dp,
                    color = Grey80,
                    shape = RoundedCornerShape(borderRadius)
                ),
            contentScale = ContentScale.Crop,
        )
        Text(
            text = "2024.03.09 (토) 17:00",
            modifier = Modifier.padding(top = 12.dp),
            style = MaterialTheme.typography.bodySmall.copy(color = Grey30)
        )
        Text(
            text = "2024 TOGETHER LUCKY CLUB",
            modifier = Modifier.padding(top = 2.dp),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            fontSize = 16.sp,
            lineHeight = 26.sp,
            fontFamily = aggroFamily,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}
