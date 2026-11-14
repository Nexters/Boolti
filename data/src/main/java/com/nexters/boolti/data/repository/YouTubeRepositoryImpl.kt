package com.nexters.boolti.data.repository

import com.nexters.boolti.data.BuildConfig
import com.nexters.boolti.data.cache.CacheKeys
import com.nexters.boolti.data.cache.CacheStore
import com.nexters.boolti.data.network.api.YouTubeService
import com.nexters.boolti.data.util.YouTubeUrlUtils
import com.nexters.boolti.domain.model.YouTubeVideo
import com.nexters.boolti.domain.repository.YouTubeRepository
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class YouTubeRepositoryImpl @Inject constructor(
    private val youtubeService: YouTubeService,
    private val cacheStore: CacheStore,
) : YouTubeRepository {

    override suspend fun getVideoInfo(videoId: String): YouTubeVideo? {
        return try {
            if (!YouTubeUrlUtils.isValidVideoId(videoId)) return null

            cacheStore.getOrFetch(CacheKeys.youTubeVideo(videoId)) {
                val response = youtubeService.getVideoInfo(
                    id = videoId,
                    key = BuildConfig.YOUTUBE_API_KEY,
                )
                // 캐시는 null을 저장하지 않으므로, 정보가 없는 영상은 예외로 넘겨 아래 catch에서 null로 바꾼다
                response.items.firstOrNull()?.toYouTubeVideo()
                    ?: throw NoSuchElementException("YouTube 영상 정보가 없어요: $videoId")
            }.copy(localId = UUID.randomUUID().toString()) // localId는 화면 목록 키라서 꺼낼 때마다 새로 붙인다
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
