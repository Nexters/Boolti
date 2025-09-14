package com.nexters.boolti.domain.usecase

import com.nexters.boolti.domain.model.YouTubeVideo
import com.nexters.boolti.domain.repository.YouTubeRepository
import javax.inject.Inject

class GetYouTubeVideoInfoUseCase @Inject constructor(
    private val youTubeRepository: YouTubeRepository,
) {
    /**
     * YouTube video ID로 동영상 정보를 조회합니다.
     * 
     * @param videoId YouTube video ID
     * @return YouTube 동영상 정보, 찾을 수 없는 경우 null
     */
    suspend operator fun invoke(videoId: String): YouTubeVideo? {
        return youTubeRepository.getVideoInfo(videoId)
    }
}
