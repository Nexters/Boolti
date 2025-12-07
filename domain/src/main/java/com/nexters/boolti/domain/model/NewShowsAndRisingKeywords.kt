package com.nexters.boolti.domain.model

import java.time.LocalDateTime

data class NewShowsAndRisingKeywords(
    val newShows: List<Show>,
    val risingKeywords: List<String>,
    val risingKeywordsTime: LocalDateTime,
)
