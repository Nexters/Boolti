package com.nexters.boolti.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nexters.boolti.presentation.extension.toDp
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.marginHorizontal

@Composable
fun TopGradientBackground(
    modifier: Modifier = Modifier,
    bgColor: Color = MaterialTheme.colorScheme.background,
    gradientHeight: Dp = 16.dp,
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    var contentWidthDp by remember { mutableStateOf(0.dp) }

    Column(
        modifier = modifier,
    ) {
        Box(
            Modifier
                .width(contentWidthDp)
                .height(gradientHeight)
                .background(
                    brush = Brush.verticalGradient(listOf(Color.Transparent, bgColor))
                )
        )
        Box(
            Modifier
                .onGloballyPositioned { contentWidthDp = it.size.width.toDp(density) }
                .background(bgColor),
        ) {
            content()
        }
    }
}

@Preview
@Composable
private fun TopGradientBackgroundPreview() {
    BooltiTheme {
        Surface(
            color = Color.White,
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
            ) {
                TopGradientBackground(
                    modifier = Modifier
                        .align(Alignment.BottomCenter),
                ) {
                    SecondaryButton(
                        modifier = Modifier
                            .padding(horizontal = marginHorizontal)
                            .padding(bottom = 20.dp),
                        label = "예매하기",
                    ) { }
                }
            }
        }
    }
}
