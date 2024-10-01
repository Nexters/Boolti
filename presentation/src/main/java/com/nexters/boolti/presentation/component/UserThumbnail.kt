package com.nexters.boolti.presentation.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nexters.boolti.presentation.R

@Composable
fun UserThumbnail(
    size: Dp,
    model: Any?,
    modifier: Modifier = Modifier,
    defaultImage: Int = R.drawable.ic_fallback_profile,
    outlineColor: Color = MaterialTheme.colorScheme.outline,
    contentDescription: String? = null,
) {
    AsyncImage(
        modifier = modifier
            .size(size)
            .clip(shape = CircleShape)
            .border(
                width = 1.dp,
                color = outlineColor,
                shape = CircleShape,
            ),
        model = model,
        contentDescription = contentDescription,
        placeholder = painterResource(id = defaultImage),
        fallback = painterResource(id = defaultImage),
        contentScale = ContentScale.Crop,
    )
}
