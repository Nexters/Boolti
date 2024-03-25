package com.nexters.boolti.presentation.screen

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeepLinkEvent @Inject constructor() {
    private val _events = Channel<String>(capacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val events = _events.receiveAsFlow()

    suspend fun sendEvent(deepLink: String) {
        _events.send(deepLink)
    }
}
