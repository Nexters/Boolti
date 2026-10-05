package com.nexters.boolti.presentation.screen.profileedit.link

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.nexters.boolti.presentation.screen.navigation.LinkListRoute
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
class LinkEditViewModelTest : DescribeSpec({

    // toRoute()는 안에서 Bundle을 써서 JVM 테스트에서 돌지 않는다 (b/349807172)
    beforeSpec { mockkStatic("androidx.navigation.SavedStateHandleKt") }
    afterSpec { unmockkStatic("androidx.navigation.SavedStateHandleKt") }
    beforeTest { Dispatchers.setMain(UnconfinedTestDispatcher()) }
    afterTest { Dispatchers.resetMain() }

    fun createViewModel(route: LinkListRoute.LinkEdit) = LinkEditViewModel(
        savedStateHandle = SavedStateHandle().also {
            every { it.toRoute<LinkListRoute.LinkEdit>() } returns route
        },
    )

    describe("완료") {
        it("새 링크면 추가 결과를 돌려준다") {
            val viewModel = createViewModel(LinkListRoute.LinkEdit())
            viewModel.onAction(LinkEditAction.ChangeName("유튜브"))
            viewModel.onAction(LinkEditAction.ChangeUrl("https://youtube.com"))

            viewModel.onAction(LinkEditAction.Complete)

            viewModel.event.first() shouldBe LinkEditEvent.Done(LinkEditResult.Added(name = "유튜브", url = "https://youtube.com"))
        }

        it("기존 링크면 같은 id로 수정 결과를 돌려준다") {
            val viewModel = createViewModel(LinkListRoute.LinkEdit(linkId = "1", name = "인스타그램", url = "https://instagram.com"))
            viewModel.onAction(LinkEditAction.ChangeName("인스타"))

            viewModel.onAction(LinkEditAction.Complete)

            viewModel.event.first() shouldBe LinkEditEvent.Done(
                LinkEditResult.Edited(id = "1", name = "인스타", url = "https://instagram.com"),
            )
        }
    }

    describe("뒤로 가기") {
        it("직접 연 편집 화면이면 편집 화면만 닫는다") {
            val viewModel = createViewModel(LinkListRoute.LinkEdit(linkId = "1"))

            viewModel.onAction(LinkEditAction.Back)

            viewModel.event.first() shouldBe LinkEditEvent.Close
        }

        it("빈 목록이라 자동으로 열린 화면이면 목록까지 닫는다") {
            val viewModel = createViewModel(LinkListRoute.LinkEdit(closeListOnBack = true))

            viewModel.onAction(LinkEditAction.Back)

            viewModel.event.first() shouldBe LinkEditEvent.CloseWithList
        }
    }
})
