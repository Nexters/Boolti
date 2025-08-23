package com.nexters.boolti.data.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class YouTubeVideoResponse(
    @SerialName("items")
    val items: List<YouTubeVideoItem> = emptyList(),
)

@Serializable
data class YouTubeVideoItem(
    @SerialName("id")
    val id: String,
    @SerialName("snippet")
    val snippet: YouTubeVideoSnippet,
    @SerialName("contentDetails")
    val contentDetails: YouTubeContentDetails,
)

@Serializable
data class YouTubeVideoSnippet(
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String,
    @SerialName("thumbnails")
    val thumbnails: YouTubeThumbnails,
    @SerialName("channelTitle")
    val channelTitle: String,
    @SerialName("publishedAt")
    val publishedAt: String,
)

@Serializable
data class YouTubeContentDetails(
    @SerialName("duration")
    val duration: String,
)

@Serializable
data class YouTubeThumbnails(
    @SerialName("default")
    val default: YouTubeThumbnail? = null,
    @SerialName("medium")
    val medium: YouTubeThumbnail? = null,
    @SerialName("high")
    val high: YouTubeThumbnail? = null,
    @SerialName("standard")
    val standard: YouTubeThumbnail? = null,
    @SerialName("maxres")
    val maxres: YouTubeThumbnail? = null,
)

@Serializable
data class YouTubeThumbnail(
    @SerialName("url")
    val url: String,
    @SerialName("width")
    val width: Int,
    @SerialName("height")
    val height: Int,
)
