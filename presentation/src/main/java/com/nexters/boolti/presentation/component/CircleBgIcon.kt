package com.nexters.boolti.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun CircleBgIcon(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    bgColor: Color,
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(bgColor)
    ) {
        Icon(imageVector = imageVector, contentDescription = null)
    }
}
