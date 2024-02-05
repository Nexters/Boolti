package com.nexters.boolti.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

data class Show(
    val id: Long,
    val name: String,
    val date: LocalDateTime,
    val salesStartDate: LocalDate,
    val salesEndDate: LocalDate,
    val thumbnailImage: String,
)
