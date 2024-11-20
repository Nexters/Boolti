package com.nexters.boolti.presentation.screen.perforemdshows

sealed interface PerformedShowsEvent {
    data object FetchFailed : PerformedShowsEvent
}
