package com.nexters.boolti.presentation.screen.video

sealed interface VideoListEvent {
    data object Added : VideoListEvent
    data object Edited : VideoListEvent
    data object Removed : VideoListEvent
    data object Finish : VideoListEvent
}

sealed interface VideoEditEvent {
    data object Finish : VideoEditEvent
}