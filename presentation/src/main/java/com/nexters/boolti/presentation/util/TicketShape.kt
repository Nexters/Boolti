package com.nexters.boolti.presentation.util

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

class TicketShape(
    private val width: Float,
    private val height: Float,
    private val circleRadius: Float,
    private val bottomAreaHeight: Float,
) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        return Outline.Generic(
            path = ticketPath(
                width = width,
                height = height,
                circleRadius = circleRadius,
                bottomAreaHeight = bottomAreaHeight,
            )
        )
    }
}

fun ticketPath(
    width: Float,
    height: Float,
    circleRadius: Float,
    bottomAreaHeight: Float,
): Path {
    return Path().apply {
        reset()
        // 상변
        lineTo(width, 0f)
        // 우변 - 펀치 상단
        lineTo(x = width, y = height - bottomAreaHeight - circleRadius * 2)
        // 우변 펀치
        arcTo(
            rect = getTicketPunchRect(Pair(width, height - bottomAreaHeight), circleRadius),
            startAngleDegrees = -90f,
            sweepAngleDegrees = -180f,
            forceMoveTo = false,
        )
        // 우변 - 펀치 하단
        lineTo(x = width, y = height)
        // 하변
        lineTo(x = 0f, y = height)
        // 좌변 - 펀지 하단
        lineTo(x = 0f, y = height - bottomAreaHeight + circleRadius)
        // 좌변 펀치
        arcTo(
            rect = getTicketPunchRect(Pair(0f, height - bottomAreaHeight), circleRadius),
            startAngleDegrees = 90f,
            sweepAngleDegrees = -180f,
            forceMoveTo = false,
        )
        // 좌변 - 펀치 상단
        lineTo(0f, 0f)
        close()
    }
}

private fun getTicketPunchRect(center: Pair<Float, Float>, radius: Float): Rect {
    val (x, y) = center
    return Rect(
        left = x - radius,
        right = x + radius,
        top = y - radius,
        bottom = y + radius,
    )
}
