package com.nexters.boolti.data.network.response

import com.nexters.boolti.domain.model.Place
import kotlinx.serialization.Serializable

@Serializable
internal data class ConcertHallProfileResponse(
    val id: Long,
    val name: String,
    val representativeImageUrl: String? = null,
    val head: ConcertHallHeadResponse? = null,
) {
    fun toDomain(): Place = Place(
        id = id.toString(),
        name = name,
        imageUrl = representativeImageUrl,
        rentalFee = head?.rentalFeeSummary,
        capacity = head?.capacity?.let { (it.seatedCapacity + it.standingCapacity).takeIf { total -> total > 0 } },
        streetAddress = head?.location?.streetAddress,
        subwayStation = head?.subwayStations?.firstOrNull()?.stationName,
        contact = head?.contact?.phoneNumber,
    )
}

@Serializable
internal data class ConcertHallHeadResponse(
    val rentalFeeSummary: String? = null,
    val capacity: ConcertHallCapacityResponse? = null,
    val location: ConcertHallLocationResponse? = null,
    val subwayStations: List<ConcertHallSubwayStationResponse> = emptyList(),
    val contact: ConcertHallContactResponse? = null,
)

@Serializable
internal data class ConcertHallCapacityResponse(
    val seatedCapacity: Int = 0,
    val standingCapacity: Int = 0,
)

@Serializable
internal data class ConcertHallLocationResponse(
    val streetAddress: String? = null,
    val detailAddress: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
)

@Serializable
internal data class ConcertHallSubwayStationResponse(
    val id: Long,
    val stationName: String,
    val region: String? = null,
    val lines: List<ConcertHallSubwayLineResponse> = emptyList(),
)

@Serializable
internal data class ConcertHallSubwayLineResponse(
    val id: Long,
    val lineKey: String,
    val lineName: String,
    val colorHex: String? = null,
)

@Serializable
internal data class ConcertHallContactResponse(
    val websiteUrl: String? = null,
    val phoneNumber: String? = null,
    val email: String? = null,
)
