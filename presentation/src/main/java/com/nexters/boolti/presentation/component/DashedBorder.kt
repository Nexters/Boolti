package com.nexters.boolti.presentation.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nexters.boolti.presentation.theme.Grey70

/**
 * 점선 테두리를 제공하는 컴포넌트
 *
 * @param modifier Modifier
 * @param strokeColor 선의 색상
 * @param strokeWidth 선의 두께
 * @param dashLength 점선의 길이
 * @param dashGap 점선 간의 간격
 * @param cornerRadius 모서리 둥글기 (dp)
 * @param content 테두리 안에 들어갈 컨텐츠
 */
@Composable
fun DashedBorderBox(
    modifier: Modifier = Modifier,
    strokeColor: Color = MaterialTheme.colorScheme.outline,
    strokeWidth: Dp = 1.dp,
    dashLength: Dp = 4.dp,
    dashGap: Dp = 4.dp,
    cornerRadius: Dp = 0.dp,
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier.drawBehind {
            drawDashedBorder(
                strokeColor = strokeColor,
                strokeWidth = strokeWidth,
                dashLength = dashLength,
                dashGap = dashGap,
                cornerRadius = cornerRadius,
            )
        },
        contentAlignment = contentAlignment,
        content = content,
    )
}

/**
 * Modifier 확장 함수로 점선 테두리 적용
 */
fun Modifier.dashedBorder(
    strokeColor: Color = Grey70,
    strokeWidth: Dp = 1.dp,
    dashLength: Dp = 4.dp,
    dashGap: Dp = 4.dp,
    cornerRadius: Dp = 0.dp
): Modifier = this.drawBehind {
    drawDashedBorder(
        strokeColor = strokeColor,
        strokeWidth = strokeWidth,
        dashLength = dashLength,
        dashGap = dashGap,
        cornerRadius = cornerRadius,
    )
}

/**
 * Shape을 받는 버전의 Modifier 확장 함수
 */
fun Modifier.dashedBorder(
    strokeColor: Color = Grey70,
    strokeWidth: Dp = 1.dp,
    dashLength: Dp = 4.dp,
    dashGap: Dp = 4.dp,
    shape: Shape = RectangleShape,
): Modifier = this.drawBehind {
    val pathEffect = PathEffect.dashPathEffect(
        floatArrayOf(dashLength.toPx(), dashGap.toPx()),
        0f
    )

    when (shape) {
        RectangleShape -> {
            drawRect(
                color = strokeColor,
                style = Stroke(
                    width = strokeWidth.toPx(),
                    pathEffect = pathEffect
                )
            )
        }

        is RoundedCornerShape -> {
            val cornerRadiusPx = shape.topStart.toPx(size, Density(density))
            drawRoundRect(
                color = strokeColor,
                cornerRadius = CornerRadius(cornerRadiusPx),
                style = Stroke(
                    width = strokeWidth.toPx(),
                    pathEffect = pathEffect
                )
            )
        }

        else -> {
            // 기본적으로 사각형으로 처리
            drawRect(
                color = strokeColor,
                style = Stroke(
                    width = strokeWidth.toPx(),
                    pathEffect = pathEffect
                )
            )
        }
    }
}

/**
 * 점선 테두리를 그리는 내부 함수
 */
private fun DrawScope.drawDashedBorder(
    strokeColor: Color,
    strokeWidth: Dp,
    dashLength: Dp,
    dashGap: Dp,
    cornerRadius: Dp,
) {
    val pathEffect = PathEffect.dashPathEffect(
        floatArrayOf(dashLength.toPx(), dashGap.toPx()),
        0f
    )

    if (cornerRadius > 0.dp) {
        drawRoundRect(
            color = strokeColor,
            cornerRadius = CornerRadius(cornerRadius.toPx()),
            style = Stroke(
                width = strokeWidth.toPx(),
                pathEffect = pathEffect
            )
        )
    } else {
        drawRect(
            color = strokeColor,
            style = Stroke(
                width = strokeWidth.toPx(),
                pathEffect = pathEffect
            )
        )
    }
}
