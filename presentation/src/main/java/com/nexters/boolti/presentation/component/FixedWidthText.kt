package com.nexters.boolti.presentation.component

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit

@Composable
fun FixedWidthText(
    text: String,
    width: Dp,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    softWrap: Boolean = true,
    maxLines: Int = 1,
    minLines: Int = 1,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
    style: TextStyle = LocalTextStyle.current,
    scrollState: ScrollState = rememberScrollState(),
    scrollEnabled: Boolean = true,
    shadowColor: Color = MaterialTheme.colorScheme.surface,
    shadowWidth: (maxValue: Int, value: Float) -> Float = { maxValue, value ->
        (maxValue - value) * 3
    },
) {
    Text(
        text = text,
        modifier = modifier
            .width(width)
            .horizontalScroll(scrollState, enabled = scrollEnabled)
            .drawWithContent {
                drawContent()

                // 오른쪽 페이드 (오른쪽에 더 많은 콘텐츠가 있을 때)
                if (scrollState.maxValue - scrollState.value > 0) {
                    val fadeWidth = shadowWidth(scrollState.maxValue, scrollState.value.toFloat())
                    drawRect(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color.Transparent, shadowColor),
                            startX = size.width - fadeWidth,
                            endX = size.width
                        ),
                        topLeft = Offset(size.width - fadeWidth, 0f),
                        size = Size(fadeWidth, size.height),
                    )
                }

                // 왼쪽 페이드 (왼쪽에 더 많은 콘텐츠가 있을 때)
                if (scrollState.value > 0) {
                    val fadeWidth = scrollState.value.toFloat() * 3
                    drawRect(
                        brush = Brush.horizontalGradient(
                            colors = listOf(shadowColor, Color.Transparent),
                            startX = 0f,
                            endX = fadeWidth
                        ),
                        topLeft = Offset.Zero,
                        size = Size(fadeWidth, size.height),
                    )
                }
            },
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = TextOverflow.Visible,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
        onTextLayout = onTextLayout,
        style = style,
    )
}
