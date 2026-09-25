package com.nexters.boolti.data.network.response

import com.nexters.boolti.domain.model.PlaceDetail
import com.nexters.boolti.domain.model.PlaceContact
import com.nexters.boolti.domain.model.SubwayLine
import com.nexters.boolti.domain.model.SubwayStation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class PlaceDetailResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("name")
    val name: String,
    @SerialName("representativeImageUrl")
    val representativeImageUrl: String? = null,
    @SerialName("head")
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
    @SerialName("rentalFeeSummary")
    val rentalFeeSummary: String? = null,
    @SerialName("capacity")
    val capacity: ConcertHallCapacityResponse? = null,
    @SerialName("location")
    val location: ConcertHallLocationResponse? = null,
    @SerialName("subwayStations")
    val subwayStations: List<ConcertHallSubwayStationResponse> = emptyList(),
    @SerialName("contact")
    val contact: ConcertHallContactResponse? = null,
)

@Serializable
internal data class ConcertHallCapacityResponse(
    @SerialName("seatedCapacity")
    val seatedCapacity: Int = 0,
    @SerialName("standingCapacity")
    val standingCapacity: Int = 0,
)

@Serializable
internal data class ConcertHallLocationResponse(
    @SerialName("streetAddress")
    val streetAddress: String? = null,
    @SerialName("detailAddress")
    val detailAddress: String? = null,
    @SerialName("latitude")
    val latitude: Double? = null,
    @SerialName("longitude")
    val longitude: Double? = null,
)

@Serializable
internal data class ConcertHallSubwayStationResponse(
    @SerialName("id")
    val id: String,
    @SerialName("stationName")
    val stationName: String,
    @SerialName("region")
    val region: String? = null,
    @SerialName("lines")
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
    @SerialName("id")
    val id: String,
    @SerialName("lineKey")
    val lineKey: String,
    @SerialName("lineName")
    val lineName: String,
    @SerialName("textColorHex")
    val textColorHex: String,
    @SerialName("colorHex")
    val colorHex: String,
) {
    fun toDomain(): SubwayLine {
        return SubwayLine(
            id = id,
            key = lineKey,
            name = lineName,
            textColorHex = textColorHex,
            colorHex = colorHex,
        )
    }
}

@Serializable
internal data class ConcertHallContactResponse(
    @SerialName("websiteUrl")
    val websiteUrl: String? = null,
    @SerialName("phoneNumber")
    val phoneNumber: String? = null,
    @SerialName("email")
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
