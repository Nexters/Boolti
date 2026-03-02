package com.nexters.boolti.presentation.extension

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

val Int.dpToSp: TextUnit
    @Composable
    get() = with(LocalDensity.current) { this@dpToSp.dp.toSp() }

val Float.dpToSp: TextUnit
    @Composable
    get() = with(LocalDensity.current) { this@dpToSp.dp.toSp() }

val TextUnit.dpToSp: TextUnit
    @Composable
    get() = with(LocalDensity.current) { this@dpToSp.value.dp.toSp() }

@Composable
fun Dp.toPx() = with(LocalDensity.current) { toPx() }

@Composable
fun Int.toDp() = with(LocalDensity.current) { toDp() }

@Composable
fun Float.toDp() = with(LocalDensity.current) { toDp() }

fun Dp.toPx(density: Density) = with(density) { toPx() }

fun Int.toDp(density: Density) = with(density) { toDp() }

fun Float.toDp(density: Density) = with(density) { toDp() }
