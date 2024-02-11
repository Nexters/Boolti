package com.nexters.boolti.presentation.component

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun DottedDivider(
    modifier: Modifier = Modifier,
    color: Color,
    thickness: Dp,
    dash: Dp = 4.dp,
    spacedBy: Dp = 4.dp,
) {
    val length = dash.value * LocalDensity.current.density
    val space = spacedBy.value * LocalDensity.current.density
    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(length, space), 10f)
    Canvas(
        modifier = modifier,
        onDraw = {
            drawLine(
                color = color,
                start = Offset.Zero,
                end = Offset(size.width, 0f),
                strokeWidth = thickness.toPx(),
                pathEffect = pathEffect,
            )
        }
    )
}
