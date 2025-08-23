package com.nexters.boolti.data.network.response

import com.nexters.boolti.domain.model.YouTubeVideo
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
) {
    fun toYouTubeVideo(): YouTubeVideo {
        val bestThumbnail = snippet.thumbnails.let { thumbnails ->
            thumbnails.maxres?.url
                ?: thumbnails.high?.url
                ?: thumbnails.standard?.url
                ?: thumbnails.medium?.url
                ?: thumbnails.default?.url
                ?: ""
        }

        return YouTubeVideo(
            id = id,
            title = snippet.title,
            description = snippet.description,
            channelTitle = snippet.channelTitle,
            publishedAt = snippet.publishedAt,
            duration = contentDetails.duration.parseIsoDurationToTimeString(),
            thumbnailUrl = bestThumbnail,
            url = "https://www.youtube.com/watch?v=$id",
        )
    }
}

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

/**
 * ISO 8601 duration 형식을 시간 문자열로 변환합니다.
 * 예: PT15M33S -> "15:33", PT1H30M45S -> "1:30:45"
 */
internal fun String.parseIsoDurationToTimeString(): String {
    if (!startsWith("PT")) return this
    
    // PT를 제거하고 파싱
    val duration = substring(2)
    
    // 정규식으로 시간, 분, 초 추출
    val hourRegex = "(\\d+)H".toRegex()
    val minuteRegex = "(\\d+)M".toRegex()
    val secondRegex = "(\\d+)S".toRegex()
    
    val hours = hourRegex.find(duration)?.groupValues?.get(1)?.toIntOrNull() ?: 0
    val minutes = minuteRegex.find(duration)?.groupValues?.get(1)?.toIntOrNull() ?: 0
    val seconds = secondRegex.find(duration)?.groupValues?.get(1)?.toIntOrNull() ?: 0
    
    return when {
        hours > 0 -> "%d:%02d:%02d".format(hours, minutes, seconds)
        else -> "%02d:%02d".format(minutes, seconds)
    }
}
