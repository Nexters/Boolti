package com.nexters.boolti.data.network.response

import com.nexters.boolti.domain.model.Place
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class PlaceResponse(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("representativeImageUrl")
    val representativeImageUrl: String,
    @SerialName("streetAddress")
    val streetAddress: String,
    @SerialName("detailAddress")
    val detailAddress: String,
) {
    fun toPlace(): Place = Place(
        id = id,
        name = name,
        streetAddress = streetAddress.trim(),
        detailAddress = detailAddress.trim(),
        thumbnailImage = representativeImageUrl,
    )
}
