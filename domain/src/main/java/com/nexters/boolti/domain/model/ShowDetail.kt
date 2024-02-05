package com.nexters.boolti.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

data class ShowDetail(
    val id: String,
    val name: String,
    val placeName: String,
    val date: LocalDateTime,
    val runningTime: Int,
    val streetAddress: String,
    val detailAddress: String,
    val salesStartTime: LocalDate,
    val salesEndTime: LocalDate,
    val images: List<Image>,
)
