package com.nexters.boolti.presentation.extension

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

fun <T> Flow<T>.stateInUi(
    scope: CoroutineScope,
    initialValue: T
) = stateIn(scope, SharingStarted.WhileSubscribed(5000), initialValue)
