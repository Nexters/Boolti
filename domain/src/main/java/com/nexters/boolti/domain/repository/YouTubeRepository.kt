package com.nexters.boolti.domain.repository

import com.nexters.boolti.domain.model.YouTubeVideo

interface YouTubeRepository {
    
    /**
     * YouTube video ID로 동영상 정보를 조회합니다.
     * 
     * @param videoId YouTube video ID
     * @return YouTube 동영상 정보, 찾을 수 없는 경우 null
     */
    suspend fun getVideoInfo(videoId: String): YouTubeVideo?
    
    /**
     * 여러 YouTube video ID들로 동영상 정보를 일괄 조회합니다.
     * 
     * @param videoIds YouTube video ID 목록
     * @return YouTube 동영상 정보 목록 (찾을 수 없는 ID는 제외됨)
     */
    suspend fun getVideoInfoList(videoIds: List<String>): List<YouTubeVideo>
    
    /**
     * YouTube URL에서 동영상 정보를 조회합니다.
     * 
     * @param url YouTube URL
     * @return YouTube 동영상 정보, URL이 유효하지 않거나 찾을 수 없는 경우 null
     */
    suspend fun getVideoInfoByUrl(url: String): YouTubeVideo?
    
    /**
     * 여러 YouTube URL들로 동영상 정보를 일괄 조회합니다.
     * 
     * @param urls YouTube URL 목록
     * @return YouTube 동영상 정보 목록 (유효하지 않거나 찾을 수 없는 URL은 제외됨)
     */
    suspend fun getVideoInfoByUrlList(urls: List<String>): List<YouTubeVideo>
}
