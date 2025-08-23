package com.nexters.boolti.domain.usecase

import com.nexters.boolti.domain.model.YouTubeVideo
import com.nexters.boolti.domain.repository.YouTubeRepository
import javax.inject.Inject

class GetYouTubeVideoInfoListUseCase @Inject constructor(
    private val youTubeRepository: YouTubeRepository,
) {
    /**
     * 여러 YouTube video ID들로 동영상 정보를 일괄 조회합니다.
     * 
     * @param videoIds YouTube video ID 목록
     * @return YouTube 동영상 정보 목록 (찾을 수 없는 ID는 제외됨)
     */
    suspend operator fun invoke(videoIds: List<String>): List<YouTubeVideo> {
        return youTubeRepository.getVideoInfoList(videoIds)
    }
}
