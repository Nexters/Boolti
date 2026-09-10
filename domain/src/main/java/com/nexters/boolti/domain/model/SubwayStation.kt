package com.nexters.boolti.domain.model

data class SubwayStation(
    val id: String,
    val name: String,
    val lines: List<SubwayLine>
)
