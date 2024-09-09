package com.nexters.boolti.presentation.extension

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun LifecycleOwner.repeatOn(
    state: Lifecycle.State = Lifecycle.State.STARTED,
    block: suspend CoroutineScope.() -> Unit,
) {
    lifecycleScope.launch {
        repeatOnLifecycle(state, block)
    }
}

fun LifecycleOwner.repeatOnStarted(
    block: suspend CoroutineScope.() -> Unit,
) {
    repeatOn(Lifecycle.State.STARTED, block)
}
