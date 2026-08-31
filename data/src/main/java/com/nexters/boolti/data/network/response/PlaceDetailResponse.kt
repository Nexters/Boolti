package com.nexters.boolti.data.network.response

import com.nexters.boolti.domain.model.PlaceDetail
import com.nexters.boolti.domain.model.PlaceContact
import com.nexters.boolti.domain.model.SubwayLine
import com.nexters.boolti.domain.model.SubwayStation
import kotlinx.serialization.Serializable

@Serializable
internal data class PlaceDetailResponse(
    val id: Long,
    val name: String,
    val representativeImageUrl: String? = null,
    val head: ConcertHallHeadResponse? = null,
) {
    fun toDomain(): PlaceDetail = PlaceDetail(
        id = id.toString(),
        name = name,
        imageUrl = representativeImageUrl,
        rentalFee = head?.rentalFeeSummary,
        capacity = head?.capacity?.let { (it.seatedCapacity + it.standingCapacity).takeIf { total -> total > 0 } },
        streetAddress = head?.location?.streetAddress,
        subwayStations = head?.subwayStations?.map { it.toDomain() } ?: emptyList(),
        contact = head?.contact?.toDomain(),
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
    val id: String,
    val stationName: String,
    val region: String? = null,
    val lines: List<ConcertHallSubwayLineResponse> = emptyList(),
) {
    fun toDomain(): SubwayStation {
        return SubwayStation(
            id = id,
            name = stationName,
            lines = lines.map { it.toDomain() },
        )
    }
}

@Serializable
internal data class ConcertHallSubwayLineResponse(
    val id: String,
    val lineKey: String,
    val lineName: String,
    val colorHex: String? = null,
) {
    fun toDomain(): SubwayLine {
        return SubwayLine(
            id = id,
            name = lineName,
            colorHex = colorHex ?: "0x00000000",
        )
    }
}

@Serializable
internal data class ConcertHallContactResponse(
    val websiteUrl: String? = null,
    val phoneNumber: String? = null,
    val email: String? = null,
) {
    fun toDomain(): PlaceContact {
        return PlaceContact(
            websiteUrl = websiteUrl,
            phoneNumber = phoneNumber,
            email = email,
        )
    }
}
