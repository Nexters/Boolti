package com.nexters.boolti.data.network.response

import com.nexters.boolti.domain.model.Place
import kotlinx.serialization.Serializable

@Serializable
internal data class PlaceResponse(
    val id: String,
    val name: String,
    val representativeImageUrl: String,
    val streetAddress: String,
    val detailAddress: String,
) {
    fun toPlace(): Place = Place(
        id = id,
        name = name,
        streetAddress = streetAddress,
        detailAddress = detailAddress,
        thumbnailImage = representativeImageUrl,
    )
}
