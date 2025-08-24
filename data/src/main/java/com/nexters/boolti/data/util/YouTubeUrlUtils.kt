package com.nexters.boolti.data.util

import java.util.regex.Pattern

object YouTubeUrlUtils {
    
    private val YOUTUBE_URL_PATTERNS = listOf(
        // https://www.youtube.com/watch?v=VIDEO_ID
        Pattern.compile("(?:https?://)?(?:www\\.)?youtube\\.com/watch\\?v=([a-zA-Z0-9_-]+)"),
        // https://youtu.be/VIDEO_ID
        Pattern.compile("(?:https?://)?youtu\\.be/([a-zA-Z0-9_-]+)"),
        // https://www.youtube.com/embed/VIDEO_ID
        Pattern.compile("(?:https?://)?(?:www\\.)?youtube\\.com/embed/([a-zA-Z0-9_-]+)"),
        // https://www.youtube.com/v/VIDEO_ID
        Pattern.compile("(?:https?://)?(?:www\\.)?youtube\\.com/v/([a-zA-Z0-9_-]+)"),
        // https://m.youtube.com/watch?v=VIDEO_ID
        Pattern.compile("(?:https?://)?m\\.youtube\\.com/watch\\?v=([a-zA-Z0-9_-]+)")
    )

    /**
     * YouTube URL에서 video ID를 추출합니다.
     * 
     * @param url YouTube URL (다양한 형태 지원)
     * @return video ID 또는 null (유효하지 않은 URL인 경우)
     */
    fun extractVideoId(url: String?): String? {
        if (url.isNullOrBlank()) return null
        
        val trimmedUrl = url.trim()
        
        for (pattern in YOUTUBE_URL_PATTERNS) {
            val matcher = pattern.matcher(trimmedUrl)
            if (matcher.find()) {
                val videoId = matcher.group(1)
                // YouTube video ID는 11자리여야 함
                if (videoId?.length == 11) {
                    return videoId
                }
            }
        }
        
        return null
    }

    /**
     * YouTube video ID가 유효한지 확인합니다.
     * 
     * @param videoId YouTube video ID
     * @return 유효한 경우 true, 그렇지 않은 경우 false
     */
    fun isValidVideoId(videoId: String?): Boolean {
        if (videoId.isNullOrBlank()) return false
        
        // YouTube video ID는 11자리의 영문자, 숫자, _, - 조합
        val pattern = Pattern.compile("^[a-zA-Z0-9_-]{11}$")
        return pattern.matcher(videoId).matches()
    }

    /**
     * YouTube URL이 유효한지 확인합니다.
     * 
     * @param url YouTube URL
     * @return 유효한 경우 true, 그렇지 않은 경우 false
     */
    fun isValidYouTubeUrl(url: String?): Boolean {
        return extractVideoId(url) != null
    }

    /**
     * video ID로부터 썸네일 URL을 생성합니다.
     * 
     * @param videoId YouTube video ID
     * @param quality 썸네일 품질 ("default", "mqdefault", "hqdefault", "sddefault", "maxresdefault")
     * @return 썸네일 URL
     */
    fun getThumbnailUrl(videoId: String, quality: String = "mqdefault"): String {
        return "https://img.youtube.com/vi/$videoId/$quality.jpg"
    }
}
