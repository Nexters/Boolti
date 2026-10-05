package com.nexters.boolti.presentation.screen.video

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.nexters.boolti.domain.model.User
import com.nexters.boolti.domain.model.YouTubeVideo
import com.nexters.boolti.domain.usecase.GetYouTubeVideoInfoByUrlUseCase
import com.nexters.boolti.domain.usecase.GetYouTubeVideoListByUserCodeUseCase
import com.nexters.boolti.presentation.screen.navigation.VideoListRoute
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class VideoListViewModelTest : DescribeSpec({

    // toRoute()는 안에서 Bundle을 써서 JVM 테스트에서 돌지 않는다 (b/349807172)
    beforeSpec { mockkStatic("androidx.navigation.SavedStateHandleKt") }
    afterSpec { unmockkStatic("androidx.navigation.SavedStateHandleKt") }
    beforeTest { Dispatchers.setMain(UnconfinedTestDispatcher()) }
    afterTest { Dispatchers.resetMain() }

    val url = "https://youtu.be/abc"

    fun createViewModel(
        videoInfo: YouTubeVideo?,
        videos: List<YouTubeVideo> = emptyList(),
    ) = VideoListViewModel(
        savedStateHandle = SavedStateHandle().also {
            every { it.toRoute<VideoListRoute.VideoList>() } returns VideoListRoute.VideoList(userCode = "me", isEditMode = false)
        },
        getCachedUserUseCase = mockk { every { this@mockk.invoke() } returns User.My(id = "user", userCode = "me") },
        getYouTubeVideoListByUserCodeUseCase = mockk<GetYouTubeVideoListByUserCodeUseCase> {
            coEvery { this@mockk.invoke(any(), any()) } returns Result.success(videos)
        },
        getYouTubeVideoInfoByUrlUseCase = mockk<GetYouTubeVideoInfoByUrlUseCase> {
            coEvery { this@mockk.invoke(any()) } returns videoInfo
        },
        userConfigRepository = mockk(),
    )

    describe("동영상 추가") {
        it("YouTube 정보를 가져오면 그 정보로 추가한다") {
            val viewModel = createViewModel(videoInfo = YouTubeVideo.EMPTY.copy(title = "공연 영상", url = url))

            viewModel.onAction(VideoListAction.EditResultReceived(VideoEditResult.Added(url)))

            viewModel.uiState.value.videos.single().title shouldBe "공연 영상"
        }

        it("YouTube 정보를 못 가져와도 입력한 주소로 추가한다") {
            val viewModel = createViewModel(videoInfo = null)

            viewModel.onAction(VideoListAction.EditResultReceived(VideoEditResult.Added(url)))

            viewModel.uiState.value.videos.single().url shouldBe url
        }
    }

    describe("동영상 수정") {
        it("YouTube 정보를 못 가져와도 같은 자리의 동영상 주소만 바뀐다") {
            val saved = YouTubeVideo.EMPTY.copy(localId = "saved", url = url)
            val viewModel = createViewModel(videoInfo = null, videos = listOf(saved))

            viewModel.onAction(VideoListAction.EditResultReceived(VideoEditResult.Edited(localId = saved.localId, url = "https://youtu.be/new")))

            viewModel.uiState.value.videos shouldBe listOf(saved.copy(url = "https://youtu.be/new"))
        }
    }
})
