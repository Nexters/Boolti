package com.nexters.boolti.domain.usecase

import com.nexters.boolti.domain.model.YouTubeVideo
import com.nexters.boolti.domain.repository.YouTubeRepository
import javax.inject.Inject

class GetYouTubeVideoInfoByUrlListUseCase @Inject constructor(
    private val youTubeRepository: YouTubeRepository,
) {
    /**
     * 여러 YouTube URL들로 동영상 정보를 일괄 조회합니다.
     * 
     * @param urls YouTube URL 목록
     * @return YouTube 동영상 정보 목록 (유효하지 않거나 찾을 수 없는 URL은 제외됨)
     */
    suspend operator fun invoke(urls: List<String>): List<YouTubeVideo> {
        return youTubeRepository.getVideoInfoByUrlList(urls)
    }
}
