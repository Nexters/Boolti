package com.nexters.boolti.presentation.extension

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp

@Composable
fun Dp.toPx() = with(LocalDensity.current) { toPx() }

@Composable
fun Int.toDp() = with(LocalDensity.current) { toDp() }

@Composable
fun Float.toDp() = with(LocalDensity.current) { toDp() }

fun Dp.toPx(density: Density) = with(density) { toPx() }

fun Int.toDp(density: Density) = with(density) { toDp() }

fun Float.toDp(density: Density) = with(density) { toDp() }
