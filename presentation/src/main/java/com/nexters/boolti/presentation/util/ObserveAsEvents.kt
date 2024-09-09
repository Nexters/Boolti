package com.nexters.boolti.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.nexters.boolti.presentation.extension.repeatOnStarted
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

@Composable
fun <T> ObserveAsEvents(
    flow: Flow<T>,
    key1: Any? = null,
    key2: Any? = null,
    onEvent: (T) -> Unit,
) {
   val lifecycleOwner = LocalLifecycleOwner.current
   LaunchedEffect(lifecycleOwner.lifecycle, key1, key2, flow) {
       lifecycleOwner.repeatOnStarted {
           withContext(Dispatchers.Main.immediate) {
               flow.collect(onEvent)
           }
       }
   }
}
