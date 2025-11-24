package com.nexters.boolti.domain.model

import java.time.LocalDateTime

data class SearchHistory(
    val keyword: String,
    val searchedAt: LocalDateTime,
)
