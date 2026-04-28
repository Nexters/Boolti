package com.nexters.boolti.data.network.response

import com.nexters.boolti.domain.model.Place
import kotlinx.serialization.Serializable

@Serializable
internal data class PlaceResponse(
    val id: String,
    val name: String,
    val rentalFee: String? = null,
    val capacity: Int? = null,
    val streetAddress: String? = null,
    val subwayStation: String? = null,
    val contact: String? = null,
) {
    fun toDomain(): Place = Place(
        id = id,
        name = name,
        rentalFee = rentalFee,
        capacity = capacity,
        streetAddress = streetAddress,
        subwayStation = subwayStation,
        contact = contact,
    )
}
