package com.nexters.boolti.presentation.screen

import com.nexters.boolti.presentation.screen.navigation.HomeRoute
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeNavigationEvent @Inject constructor() {
    private val _events = MutableSharedFlow<HomeRoute>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val events = _events.asSharedFlow()

    suspend fun sendEvent(route: HomeRoute) {
        _events.emit(route)
    }
}
