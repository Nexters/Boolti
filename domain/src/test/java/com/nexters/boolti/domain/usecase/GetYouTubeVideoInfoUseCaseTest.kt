package com.nexters.boolti.domain.usecase

import com.nexters.boolti.domain.model.YouTubeVideo
import com.nexters.boolti.domain.repository.YouTubeRepository
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

class GetYouTubeVideoInfoUseCaseTest : DescribeSpec({

    val youTubeRepository = mockk<YouTubeRepository>()
    val useCase = GetYouTubeVideoInfoUseCase(youTubeRepository)

    val mockYouTubeVideo = YouTubeVideo(
        id = "eP4ga_fNm-E",
        title = "Test Video Title",
        description = "Test Video Description",
        channelTitle = "Test Channel",
        publishedAt = "2023-01-01T00:00:00Z",
        duration = "PT3M33S",
        thumbnailUrl = "https://img.youtube.com/vi/eP4ga_fNm-E/hqdefault.jpg"
    )

    describe("invoke") {
        context("유효한 video ID가 주어졌을 때") {
            it("Repository를 통해 YouTube 동영상 정보를 반환한다") {
                // given
                val videoId = "eP4ga_fNm-E"
                coEvery { youTubeRepository.getVideoInfo(videoId) } returns mockYouTubeVideo

                // when
                val result = useCase(videoId)

                // then
                result shouldBe mockYouTubeVideo
                coVerify { youTubeRepository.getVideoInfo(videoId) }
            }
        }

        context("존재하지 않는 video ID가 주어졌을 때") {
            it("null을 반환한다") {
                // given
                val videoId = "nonexistent"
                coEvery { youTubeRepository.getVideoInfo(videoId) } returns null

                // when
                val result = useCase(videoId)

                // then
                result shouldBe null
                coVerify { youTubeRepository.getVideoInfo(videoId) }
            }
        }
    }
})
