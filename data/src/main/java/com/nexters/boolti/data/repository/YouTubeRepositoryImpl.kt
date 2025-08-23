package com.nexters.boolti.data.repository

import com.nexters.boolti.data.BuildConfig
import com.nexters.boolti.data.network.api.YouTubeService
import com.nexters.boolti.data.util.YouTubeUrlUtils
import com.nexters.boolti.data.util.toYouTubeVideo
import com.nexters.boolti.domain.model.YouTubeVideo
import com.nexters.boolti.domain.repository.YouTubeRepository
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
                key = BuildConfig.YOUTUBE_API_KEY
            )
            
            response.items.firstOrNull()?.toYouTubeVideo()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getVideoInfoList(videoIds: List<String>): List<YouTubeVideo> {
        return try {
            val validVideoIds = videoIds.filter { YouTubeUrlUtils.isValidVideoId(it) }
            if (validVideoIds.isEmpty()) return emptyList()
            
            val response = youtubeService.getVideoInfo(
                id = validVideoIds.joinToString(","),
                key = BuildConfig.YOUTUBE_API_KEY
            )
            
            response.items.map { it.toYouTubeVideo() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getVideoInfoByUrl(url: String): YouTubeVideo? {
        val videoId = YouTubeUrlUtils.extractVideoId(url) ?: return null
        return getVideoInfo(videoId)
    }

    override suspend fun getVideoInfoByUrlList(urls: List<String>): List<YouTubeVideo> {
        val videoIds = urls.mapNotNull { YouTubeUrlUtils.extractVideoId(it) }
        return getVideoInfoList(videoIds)
    }
}
