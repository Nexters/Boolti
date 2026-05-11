package com.nexters.boolti.domain.model

data class Place(
    val id: String,
    val name: String,
    val imageUrl: String?,
    val rentalFee: String?,
    val capacity: Int?,
    val streetAddress: String?,
    val subwayStation: String?,
    val contact: String?,
)
