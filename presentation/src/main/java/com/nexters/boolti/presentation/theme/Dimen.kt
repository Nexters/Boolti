package com.nexters.boolti.presentation.theme

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val marginHorizontal: Dp = 20.dp
val subTextPadding: Dp = 6.dp

val statusBarPaddingValues: PaddingValues
    @Composable
    get() = WindowInsets.statusBars.asPaddingValues()

val statusBarHeight: Dp
    @Composable
    get() = statusBarPaddingValues.calculateTopPadding()
