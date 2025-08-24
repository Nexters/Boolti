package com.nexters.boolti.domain.usecase

import com.nexters.boolti.domain.model.YouTubeVideo
import com.nexters.boolti.domain.repository.YouTubeRepository
import javax.inject.Inject

class GetYouTubeVideoInfoByUrlUseCase @Inject constructor(
    private val youTubeRepository: YouTubeRepository,
) {
    /**
     * YouTube URL에서 동영상 정보를 조회합니다.
     * 
     * @param url YouTube URL
     * @return YouTube 동영상 정보, URL이 유효하지 않거나 찾을 수 없는 경우 null
     */
    suspend operator fun invoke(url: String): YouTubeVideo? {
        return youTubeRepository.getVideoInfoByUrl(url)
    }
}
