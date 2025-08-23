package com.nexters.boolti.domain.model

data class YouTubeVideo(
    val id: String,
    val title: String,
    val description: String,
    val channelTitle: String,
    val publishedAt: String,
    val duration: String,
    val thumbnailUrl: String,
    val url: String,
) {
    companion object {
        val EMPTY = YouTubeVideo(
            id = "",
            title = "",
            description = "",
            channelTitle = "",
            publishedAt = "",
            duration = "",
            thumbnailUrl = "",
            url = "",
        )
    }
}
