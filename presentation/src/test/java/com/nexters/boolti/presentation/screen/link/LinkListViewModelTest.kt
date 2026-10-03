package com.nexters.boolti.presentation.screen.link

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.nexters.boolti.domain.model.Link
import com.nexters.boolti.domain.model.User
import com.nexters.boolti.domain.repository.MemberRepository
import com.nexters.boolti.presentation.screen.navigation.LinkListRoute
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class LinkListViewModelTest : DescribeSpec({

    // toRoute()는 안에서 Bundle을 써서 JVM 테스트에서 돌지 않는다 (b/349807172)
    beforeSpec { mockkStatic("androidx.navigation.SavedStateHandleKt") }
    afterSpec { unmockkStatic("androidx.navigation.SavedStateHandleKt") }
    beforeTest { Dispatchers.setMain(UnconfinedTestDispatcher()) }
    afterTest { Dispatchers.resetMain() }

    val savedLink = Link(id = "1", name = "인스타그램", url = "https://instagram.com")

    fun createViewModel(
        links: List<Link> = listOf(savedLink),
        isEditMode: Boolean = false,
    ) = LinkListViewModel(
        savedStateHandle = SavedStateHandle().also {
            every { it.toRoute<LinkListRoute.LinkListRoot>() } returns LinkListRoute.LinkListRoot(userCode = "me", isEditMode = isEditMode)
        },
        getCachedUserUseCase = mockk { every { this@mockk.invoke() } returns User.My(id = "user", userCode = "me") },
        memberRepository = mockk<MemberRepository> {
            coEvery { getLinks(any(), any()) } returns Result.success(links)
        },
        userConfigRepository = mockk(),
    )

    describe("링크 불러오기") {
        it("내 링크가 비어 있으면 편집 모드로 바꾸고 편집 화면으로 이동한다") {
            val viewModel = createViewModel(links = emptyList())

            viewModel.uiState.value.editing shouldBe true
            viewModel.event.first() shouldBe LinkListEvent.NavigateToEdit(isEditMode = false)
        }
    }

    describe("링크 추가·수정·삭제") {
        it("새 링크를 완료하면 목록 맨 위에 추가하고 편집 화면을 닫는다") {
            val viewModel = createViewModel()

            viewModel.onAction(LinkListAction.ClickAddLink)
            viewModel.onAction(LinkListAction.ChangeLinkName("유튜브"))
            viewModel.onAction(LinkListAction.ChangeLinkUrl("https://youtube.com"))
            viewModel.onAction(LinkListAction.CompleteLink)

            viewModel.uiState.value.links.map { it.name } shouldContainExactly listOf("유튜브", "인스타그램")
            viewModel.event.take(3).toList() shouldContainExactly listOf(
                LinkListEvent.NavigateToEdit(isEditMode = false),
                LinkListEvent.CloseEdit,
                LinkListEvent.Added,
            )
        }

        it("기존 링크를 고치면 같은 자리의 링크가 바뀐다") {
            val viewModel = createViewModel()

            viewModel.onAction(LinkListAction.ClickLink(savedLink.id))
            viewModel.onAction(LinkListAction.ChangeLinkName("인스타"))
            viewModel.onAction(LinkListAction.CompleteLink)

            viewModel.uiState.value.links shouldContainExactly listOf(savedLink.copy(name = "인스타"))
        }

        it("링크를 지우면 목록에서 빠진다") {
            val viewModel = createViewModel()

            viewModel.onAction(LinkListAction.ClickLink(savedLink.id))
            viewModel.onAction(LinkListAction.RemoveLink)

            viewModel.uiState.value.links shouldBe emptyList()
        }
    }

    describe("뒤로 가기") {
        it("편집 모드가 아니면 화면을 닫는다") {
            val viewModel = createViewModel()

            viewModel.onAction(LinkListAction.Back)

            viewModel.event.first() shouldBe LinkListEvent.Finish
        }

        it("편집 중 바뀐 게 있으면 나가기 확인 창을 띄운다") {
            val viewModel = createViewModel()
            viewModel.onAction(LinkListAction.StartEditing)
            viewModel.onAction(LinkListAction.ClickLink(savedLink.id))
            viewModel.onAction(LinkListAction.RemoveLink)

            viewModel.onAction(LinkListAction.Back)

            viewModel.uiState.value.showExitAlertDialog shouldBe true
        }

        it("편집 중 바뀐 게 없으면 편집 모드만 끈다") {
            val viewModel = createViewModel()
            viewModel.onAction(LinkListAction.StartEditing)

            viewModel.onAction(LinkListAction.Back)

            viewModel.uiState.value.editing shouldBe false
            viewModel.event.first() shouldBe LinkListEvent.CloseEdit
        }

        it("편집하다 원래처럼 빈 목록으로 돌아오면 화면을 닫는다") {
            val viewModel = createViewModel(links = emptyList())
            viewModel.onAction(LinkListAction.ClickAddLink)
            viewModel.onAction(LinkListAction.ChangeLinkName("유튜브"))
            viewModel.onAction(LinkListAction.ChangeLinkUrl("https://youtube.com"))
            viewModel.onAction(LinkListAction.CompleteLink)
            viewModel.onAction(LinkListAction.ClickLink(viewModel.uiState.value.links.single().id))
            viewModel.onAction(LinkListAction.RemoveLink)

            viewModel.onAction(LinkListAction.Back)

            viewModel.event.take(8).toList().last() shouldBe LinkListEvent.Finish
        }

        it("편집 모드로 바로 들어와 바뀐 게 없으면 편집·목록 화면을 모두 닫는다") {
            val viewModel = createViewModel(isEditMode = true)

            viewModel.onAction(LinkListAction.Back)

            viewModel.event.take(2).toList() shouldContainExactly listOf(
                LinkListEvent.CloseEdit,
                LinkListEvent.Finish,
            )
        }
    }
})
