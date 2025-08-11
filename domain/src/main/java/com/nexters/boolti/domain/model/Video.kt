package com.nexters.boolti.domain.model

data class Video(
    val id: String,
    val link: String,
    val thumbnail: List<String>,
    val title: String,
    val time: String,
)

private val LoadingVideo = Video(
    "LOADING",
    "",
    thumbnail = emptyList(),
    title = "",
    time = "-",
)

private val UnknownVideo = Video(
    id = "UNKNOWN",
    "",
    thumbnail = emptyList(),
    title = "알 수 없는 동영상",
    time = "-"
)

val Video.Loading
    get() = LoadingVideo

val Video.Unknown
    get() = UnknownVideo
