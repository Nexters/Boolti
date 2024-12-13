package com.nexters.boolti.presentation.screen.showdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Indicator(
    position: Int,
    size: Int,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        (0 until size).forEach { index ->
            val opacity = if (index == position) 1f else 0.5f
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(7.dp)
                    .clip(shape = RoundedCornerShape(4.dp))
                    .background(Color.White.copy(alpha = opacity))
            )
        }
    }
}