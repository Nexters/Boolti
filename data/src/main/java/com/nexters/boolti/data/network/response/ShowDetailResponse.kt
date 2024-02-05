package com.nexters.boolti.data.network.response

data class ShowDetailResponse(
    val id: String,
    val name: String,
    val placeName: String,
    val date: String,
    val runningTime: Int,
    val streetAddress: String,
    val detailAddress: String,
    val salesStartTime: String,
    val salesEndTime: String,
    val showImg: List<ImageResponse>,
)