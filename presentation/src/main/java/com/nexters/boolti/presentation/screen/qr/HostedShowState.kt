package com.nexters.boolti.presentation.screen.qr

import com.nexters.boolti.domain.model.Show

data class HostedShowState(
    val error: Boolean,
    val loading: Boolean,
    val shows: List<Show>,
) {
    companion object {
        val EMPTY = HostedShowState(
            error = false,
            loading = false,
            shows = emptyList(),
        )
    }
}
