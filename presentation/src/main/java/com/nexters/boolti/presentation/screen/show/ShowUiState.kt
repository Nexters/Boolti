package com.nexters.boolti.presentation.screen.show

import com.nexters.boolti.domain.model.Show

data class ShowUiState(
    val shows: List<Show> = emptyList(),
)