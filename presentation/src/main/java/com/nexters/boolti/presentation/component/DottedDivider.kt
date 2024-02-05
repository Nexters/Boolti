package com.nexters.boolti.presentation.component

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.Dp

@Composable
fun DottedDivider(
    modifier: Modifier = Modifier,
    color: Color,
    thickness: Dp,
) {
    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 10f), 10f)
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
