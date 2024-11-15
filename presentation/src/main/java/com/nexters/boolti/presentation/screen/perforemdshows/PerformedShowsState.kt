package com.nexters.boolti.presentation.screen.perforemdshows

import com.nexters.boolti.domain.model.Show

data class PerformedShowsState(
    val loading: Boolean = false,
    val shows: List<Show> = emptyList(),
)
