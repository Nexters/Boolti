package com.nexters.boolti.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsIgnoringVisibility
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.zIndex

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StatusBarCover(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.background,
) {
    Box(
        modifier = modifier
            .zIndex(1f)
            .background(color)
            .fillMaxWidth()
            .windowInsetsTopHeight(WindowInsets.statusBarsIgnoringVisibility),
    )
}
