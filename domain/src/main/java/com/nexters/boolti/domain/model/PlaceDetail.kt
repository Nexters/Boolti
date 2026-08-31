package com.nexters.boolti.domain.model

data class PlaceDetail(
    val id: String,
    val name: String,
    val imageUrl: String?,
    val rentalFee: String?,
    val capacity: Int?,
    val streetAddress: String?,
    val subwayStations: List<SubwayStation>,
    val contact: PlaceContact?,
)
