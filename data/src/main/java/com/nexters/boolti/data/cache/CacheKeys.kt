package com.nexters.boolti.data.cache

import com.nexters.boolti.domain.model.Gift
import com.nexters.boolti.domain.model.ImagePair
import com.nexters.boolti.domain.model.PlaceImage
import com.nexters.boolti.domain.model.PreQuestion
import com.nexters.boolti.domain.model.Show
import com.nexters.boolti.domain.model.YouTubeVideo
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/** 키 문자열은 "종류:id" 형식으로 짓는다 */
internal object CacheKeys {
    fun gift(giftUuid: String) = cacheKey<Gift>("gift:$giftUuid", 60.seconds)

    val giftImages = cacheKey<List<ImagePair>>("giftImages", 1.hours)

    fun preQuestions(showId: String) = cacheKey<List<PreQuestion>>("preQuestions:$showId", 60.seconds)

    fun performedShows(userCode: String) = cacheKey<List<Show>>("performedShows:$userCode", 5.minutes)

    fun placeImages(placeId: String) = cacheKey<List<PlaceImage>>("placeImages:$placeId", 5.minutes)

    fun youTubeVideo(videoId: String) = cacheKey<YouTubeVideo>("youTubeVideo:$videoId", 1.hours)
}
