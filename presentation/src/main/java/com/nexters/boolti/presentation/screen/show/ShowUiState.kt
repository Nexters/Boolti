package com.nexters.boolti.presentation.screen.show

import com.nexters.boolti.domain.model.Show

data class ShowUiState(
    val keyword: String = "",
    val shows: List<Show> = emptyList(),
    val hasPendingReservation: Boolean = false,
)