package com.nexters.boolti.data.network.response

import com.nexters.boolti.domain.model.Place
import kotlinx.serialization.Serializable

@Serializable
internal data class ConcertHallResponse(
    val id: Long = 0L,
    val name: String = "",
    val representativeImageUrl: String = "",
    val streetAddress: String = "",
    val detailAddress: String = "",
) {
    fun toPlace(): Place = Place(
        id = id.toString(),
        name = name,
        streetAddress = streetAddress,
        detailAddress = detailAddress,
        thumbnailImage = representativeImageUrl,
    )
}
