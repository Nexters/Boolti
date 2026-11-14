package com.nexters.boolti.presentation.screen.video

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.nexters.boolti.presentation.screen.navigation.VideoListRoute
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class VideoEditViewModelTest : DescribeSpec({

    // toRoute()는 안에서 Bundle을 써서 JVM 테스트에서 돌지 않는다 (b/349807172)
    beforeSpec { mockkStatic("androidx.navigation.SavedStateHandleKt") }
    afterSpec { unmockkStatic("androidx.navigation.SavedStateHandleKt") }
    beforeTest { Dispatchers.setMain(UnconfinedTestDispatcher()) }
    afterTest { Dispatchers.resetMain() }

    val saved = VideoListRoute.VideoEdit(localId = "saved", url = "https://youtu.be/abc")

    fun createViewModel(route: VideoListRoute.VideoEdit) = VideoEditViewModel(
        savedStateHandle = SavedStateHandle().also {
            every { it.toRoute<VideoListRoute.VideoEdit>() } returns route
        },
    )

    it("주소를 바꾸기 전에는 완료할 수 없다") {
        val viewModel = createViewModel(saved)

        viewModel.uiState.value.completeEnabled shouldBe false
        viewModel.onAction(VideoEditAction.ChangeUrl("https://youtu.be/new"))
        viewModel.uiState.value.completeEnabled shouldBe true
    }

    it("기존 동영상을 완료하면 같은 localId로 수정 결과를 돌려준다") {
        val viewModel = createViewModel(saved)
        viewModel.onAction(VideoEditAction.ChangeUrl("https://youtu.be/new"))

        viewModel.onAction(VideoEditAction.Complete)

        viewModel.event.first() shouldBe VideoEditEvent.Done(
            VideoEditResult.Edited(localId = "saved", url = "https://youtu.be/new"),
        )
    }

    it("빈 목록이라 자동으로 열린 화면에서 뒤로 가면 목록까지 닫는다") {
        val viewModel = createViewModel(VideoListRoute.VideoEdit(closeListOnBack = true))

        viewModel.onAction(VideoEditAction.Back)

        viewModel.event.first() shouldBe VideoEditEvent.CloseWithList
    }
})
