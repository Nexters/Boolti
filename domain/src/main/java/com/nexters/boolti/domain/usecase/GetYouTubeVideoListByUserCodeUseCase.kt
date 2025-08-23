package com.nexters.boolti.domain.usecase

import com.nexters.boolti.domain.model.YouTubeVideo
import com.nexters.boolti.domain.repository.MemberRepository
import javax.inject.Inject

class GetYouTubeVideoListByUserCodeUseCase @Inject constructor(
    private val memberRepository: MemberRepository,
    private val getYouTubeVideoInfoByUrlListUseCase: GetYouTubeVideoInfoByUrlListUseCase,
) {
    suspend operator fun invoke(
        userCode: String,
        refresh: Boolean = false,
    ): Result<List<YouTubeVideo>> {
        return memberRepository.getVideoLinks(userCode, refresh)
            .mapCatching { videoUrls ->
                if (videoUrls.isEmpty()) {
                    emptyList<YouTubeVideo>()
                } else {
                    getYouTubeVideoInfoByUrlListUseCase(videoUrls)
                }
            }
    }
}
