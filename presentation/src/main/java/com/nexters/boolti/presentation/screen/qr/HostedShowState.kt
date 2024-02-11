package com.nexters.boolti.presentation.screen.qr

import com.nexters.boolti.domain.model.Show

data class HostedShowState(
    val loading: Boolean = false,
    val shows: List<Show> = emptyList(),
)
