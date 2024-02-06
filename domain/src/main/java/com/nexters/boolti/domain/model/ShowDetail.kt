package com.nexters.boolti.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

data class ShowDetail(
    val id: String = "0",
    val name: String = "",
    val placeName: String = "",
    val date: LocalDateTime = LocalDateTime.now(),
    val runningTime: Int = 0,
    val streetAddress: String = "",
    val detailAddress: String = "",
    val notice: String = "",
    val salesStartDate: LocalDate = LocalDate.now(),
    val salesEndDate: LocalDate = LocalDate.now(),
    val images: List<Image> = emptyList(),
)
