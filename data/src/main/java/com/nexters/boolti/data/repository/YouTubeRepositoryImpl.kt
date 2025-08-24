package com.nexters.boolti.data.repository

import com.nexters.boolti.data.BuildConfig
import com.nexters.boolti.data.network.api.YouTubeService
import com.nexters.boolti.data.util.YouTubeUrlUtils
import com.nexters.boolti.domain.model.YouTubeVideo
import com.nexters.boolti.domain.repository.YouTubeRepository
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class YouTubeRepositoryImpl @Inject constructor(
    private val youtubeService: YouTubeService,
) : YouTubeRepository {

    override suspend fun getVideoInfo(videoId: String): YouTubeVideo? {
        return try {
            if (!YouTubeUrlUtils.isValidVideoId(videoId)) return null

            val response = youtubeService.getVideoInfo(
                id = videoId,
                key = BuildConfig.YOUTUBE_API_KEY,
            )

            response.items.firstOrNull()?.toYouTubeVideo()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getVideoInfoList(videoIds: List<String>): List<YouTubeVideo> {
        return videoIds.map { videoId ->
            if (YouTubeUrlUtils.isValidVideoId(videoId)) {
                getVideoInfo(videoId) ?: createInvalidVideoById(videoId)
            } else {
                createInvalidVideoById(videoId)
            }
        }
    }

    override suspend fun getVideoInfoByUrl(url: String): YouTubeVideo? {
        val videoId = YouTubeUrlUtils.extractVideoId(url) ?: return null
        return getVideoInfo(videoId)
    }

    override suspend fun getVideoInfoByUrlList(urls: List<String>): List<YouTubeVideo> {
        return urls.map { url ->
            val videoId = YouTubeUrlUtils.extractVideoId(url)
            if (videoId != null) {
                getVideoInfo(videoId) ?: createInvalidVideo(url)
            } else {
                createInvalidVideo(url)
            }
        }
    }

    private fun createInvalidVideo(url: String): YouTubeVideo {
        return YouTubeVideo.EMPTY.copy(
            localId = UUID.randomUUID().toString(),
            url = url,
        )
    }

    private fun createInvalidVideoById(videoId: String): YouTubeVideo {
        return YouTubeVideo.EMPTY.copy(
            localId = UUID.randomUUID().toString(),
            id = videoId,
            url = "https://www.youtube.com/watch?v=$videoId",
        )
    }
}
