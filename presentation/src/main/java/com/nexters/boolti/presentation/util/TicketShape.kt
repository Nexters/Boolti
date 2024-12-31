package com.nexters.boolti.presentation.util

import androidx.compose.ui.geometry.Offset
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
    private val cornerRadius: Float,
    private val bottomAreaHeight: Float,
) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        return Outline.Generic(
            path = ticketPath(
                width = width,
                height = height,
                circleRadius = circleRadius,
                cornerRadius = cornerRadius,
                bottomAreaHeight = bottomAreaHeight,
            )
        )
    }
}

fun ticketPath(
    width: Float,
    height: Float,
    circleRadius: Float,
    cornerRadius: Float,
    bottomAreaHeight: Float,
): Path {
    return Path().apply {
        reset()

        // 상변
        arcTo(
            rect = Rect(
                topLeft = Offset.Zero,
                bottomRight = Offset(cornerRadius * 2, cornerRadius * 2),
            ),
            startAngleDegrees = 180f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false,
        )
        lineTo(width - cornerRadius * 2, 0f)
        arcTo(
            rect = Rect(
                topLeft = Offset(width - cornerRadius * 2, 0f),
                bottomRight = Offset(width, cornerRadius * 2),
            ),
            startAngleDegrees = -90f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false,
        )

        // 우변
        lineTo(x = width, y = height - bottomAreaHeight - circleRadius)
        arcTo(
            rect = getTicketPunchRect(Offset(width, height - bottomAreaHeight), circleRadius),
            startAngleDegrees = -90f,
            sweepAngleDegrees = -180f,
            forceMoveTo = false,
        )
        lineTo(x = width, y = height - cornerRadius * 2)

        // 하변
        arcTo(
            rect = Rect(
                topLeft = Offset(width - cornerRadius * 2, height - cornerRadius * 2),
                bottomRight = Offset(width, height),
            ),
            startAngleDegrees = 0f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false,
        )
        lineTo(cornerRadius * 2, height)
        arcTo(
            rect = Rect(
                topLeft = Offset(0f, height - cornerRadius * 2),
                bottomRight = Offset(cornerRadius * 2, height),
            ),
            startAngleDegrees = 90f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false,
        )

        // 좌변
        lineTo(0f, height - bottomAreaHeight + circleRadius)
        arcTo(
            rect = getTicketPunchRect(Offset(0f, height - bottomAreaHeight), circleRadius),
            startAngleDegrees = 90f,
            sweepAngleDegrees = -180f,
            forceMoveTo = false,
        )
        close()
    }
}

private fun getTicketPunchRect(center: Offset, radius: Float): Rect {
    val (x, y) = center
    return Rect(
        left = x - radius,
        right = x + radius,
        top = y - radius,
        bottom = y + radius,
    )
}
