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

@Composable
fun CircleBgIcon(
    modifier: Modifier = Modifier,
    painter: Painter,
    bgColor: Color,
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(bgColor)
    ) {
        Icon(painter = painter, contentDescription = null)
    }
}
