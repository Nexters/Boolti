package com.nexters.boolti.presentation.screen.showdetail

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection

class ReservationPeriodShape(
    private val radius: Dp,
    private val circleRadius: Dp,
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val radiusPx = radius.toPx(density)
        val circleRadiusPx = circleRadius.toPx(density)

        return Outline.Generic(
            path = Path().apply {
                // 좌상귀
                arcTo(
                    rect = Rect(
                        topLeft = Offset(0f, 0f),
                        bottomRight = Offset(2 * radiusPx, 2 * radiusPx),
                    ),
                    startAngleDegrees = 180f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false,
                )

                // 상변
                lineTo(size.width - radiusPx, 0f)

                // 우상귀
                arcTo(
                    rect = Rect(
                        topLeft = Offset(size.width - 2 * radiusPx, 0f),
                        bottomRight = Offset(size.width, 2 * radiusPx),
                    ),
                    startAngleDegrees = 270f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false,
                )

                // 우상변
                lineTo(size.width, size.height - circleRadiusPx)

                // 오른쪽 잘록한 허리
                arcTo(
                    rect = createRect(
                        center = Offset(size.width, size.height / 2),
                        radius = circleRadiusPx
                    ),
                    startAngleDegrees = 270f,
                    sweepAngleDegrees = -180f,
                    forceMoveTo = false,
                )

                // 우하변
                lineTo(size.width, size.height - radiusPx)

                // 우하귀
                arcTo(
                    rect = Rect(
                        topLeft = Offset(size.width - 2 * radiusPx, size.height - 2 * radiusPx),
                        bottomRight = Offset(size.width, size.height),
                    ),
                    startAngleDegrees = 0f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false,
                )

                // 하변
                lineTo(0f + radiusPx, size.height)

                // 좌하귀
                arcTo(
                    rect = Rect(
                        topLeft = Offset(0f, size.height - 2 * radiusPx),
                        bottomRight = Offset(2 * radiusPx, size.height),
                    ),
                    startAngleDegrees = 90f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false,
                )

                // 좌하변
                lineTo(0f, size.height / 2 + circleRadiusPx)

                // 왼쪽 잘록한 허리
                arcTo(
                    rect = createRect(
                        center = Offset(0f, size.height / 2),
                        radius = circleRadiusPx
                    ),
                    startAngleDegrees = 90f,
                    sweepAngleDegrees = -180f,
                    forceMoveTo = false,
                )

                // 좌상변
                lineTo(0f, radiusPx)
            }
        )
    }

    private fun createRect(center: Offset, radius: Float): Rect {
        return Rect(
            topLeft = center - Offset(radius, radius),
            bottomRight = center + Offset(radius, radius),
        )
    }
}

private fun Dp.toPx(density: Density): Float = value * density.density