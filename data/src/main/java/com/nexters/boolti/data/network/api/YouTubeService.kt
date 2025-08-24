package com.nexters.boolti.data.network.api

import com.nexters.boolti.data.network.response.YouTubeVideoResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface YouTubeService {
    
    /**
     * YouTube Data API v3를 사용하여 동영상 정보를 조회합니다.
     * 
     * @param part 응답에 포함할 속성 (snippet, contentDetails 등)
     * @param id 동영상 ID (쉼표로 구분하여 여러 개 가능)
     * @param key API 키
     * @return YouTube 동영상 정보 응답
     */
    @GET("videos")
    suspend fun getVideoInfo(
        @Query("part") part: String = "snippet,contentDetails",
        @Query("id") id: String,
        @Query("key") key: String
    ): YouTubeVideoResponse
}
