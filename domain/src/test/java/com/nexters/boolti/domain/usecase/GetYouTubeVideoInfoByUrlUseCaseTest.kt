package com.nexters.boolti.domain.usecase

import com.nexters.boolti.domain.model.YouTubeVideo
import com.nexters.boolti.domain.repository.YouTubeRepository
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

class GetYouTubeVideoInfoByUrlUseCaseTest : DescribeSpec({

    val youTubeRepository = mockk<YouTubeRepository>()
    val useCase = GetYouTubeVideoInfoByUrlUseCase(youTubeRepository)

    val mockYouTubeVideo = YouTubeVideo(
        localId = "test-local-id",
        id = "eP4ga_fNm-E",
        title = "Test Video Title",
        description = "Test Video Description",
        channelTitle = "Test Channel",
        publishedAt = "2023-01-01T00:00:00Z",
        duration = "PT3M33S",
        thumbnailUrl = "https://img.youtube.com/vi/eP4ga_fNm-E/hqdefault.jpg",
        url = "https://www.youtube.com/watch?v=eP4ga_fNm-E"
    )

    describe("invoke") {
        context("유효한 YouTube URL이 주어졌을 때") {
            it("Repository를 통해 YouTube 동영상 정보를 반환한다") {
                // given
                val url = "https://www.youtube.com/watch?v=eP4ga_fNm-E"
                coEvery { youTubeRepository.getVideoInfoByUrl(url) } returns mockYouTubeVideo

                // when
                val result = useCase(url)

                // then
                result shouldBe mockYouTubeVideo
                coVerify { youTubeRepository.getVideoInfoByUrl(url) }
            }
        }

        context("유효하지 않은 URL이 주어졌을 때") {
            it("null을 반환한다") {
                // given
                val url = "https://www.google.com"
                coEvery { youTubeRepository.getVideoInfoByUrl(url) } returns null

                // when
                val result = useCase(url)

                // then
                result shouldBe null
                coVerify { youTubeRepository.getVideoInfoByUrl(url) }
            }
        }
    }
})
