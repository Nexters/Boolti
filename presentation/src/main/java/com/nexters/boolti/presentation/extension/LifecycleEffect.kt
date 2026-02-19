package com.nexters.boolti.presentation.extension

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun OnLifecycleEvent(
    event: Lifecycle.Event,
    onEvent: () -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, e ->
            if (e == event) onEvent()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
}

@Composable
fun OnResume(onResume: () -> Unit) {
    OnLifecycleEvent(Lifecycle.Event.ON_RESUME, onResume)
}

@Composable
fun OnStart(onStart: () -> Unit) {
    OnLifecycleEvent(Lifecycle.Event.ON_START, onStart)
}
