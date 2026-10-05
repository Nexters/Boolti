package com.nexters.boolti.presentation.screen.profileedit.sns

import com.nexters.boolti.domain.model.Sns
import com.nexters.boolti.domain.model.User
import com.nexters.boolti.domain.repository.UserConfigRepository
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class SnsEditViewModelTest : DescribeSpec({

    beforeTest { Dispatchers.setMain(UnconfinedTestDispatcher()) }
    afterTest { Dispatchers.resetMain() }

    fun createViewModel(repository: UserConfigRepository = mockk()) = SnsEditViewModel(
        getCachedUserUseCase = mockk {
            every { this@mockk.invoke() } returns User.My(
                id = "user",
                sns = listOf(Sns(id = "1", type = Sns.SnsType.INSTAGRAM, username = "boolti")),
            )
        },
        userConfigRepository = repository,
    )

    it("저장된 SNS 계정을 처음 값으로 불러온다") {
        val viewModel = createViewModel()

        viewModel.uiState.value.instagramUsername shouldBe "boolti"
        viewModel.uiState.value.youtubeUsername shouldBe ""
    }

    it("계정을 바꾸고 뒤로 가면 나가기 확인 팝업을 띄운다") {
        val viewModel = createViewModel()

        viewModel.onAction(SnsEditAction.ChangeYoutubeUsername("boolti"))
        viewModel.onAction(SnsEditAction.ClickBack)

        viewModel.uiState.value.showExitAlertDialog shouldBe true
    }

    it("계정 하나만 형식이 틀려도 팝업 없이 나간다") {
        val viewModel = createViewModel()

        viewModel.onAction(SnsEditAction.ChangeInstagramUsername("@boolti"))
        viewModel.onAction(SnsEditAction.ClickBack)

        viewModel.uiState.value.showExitAlertDialog shouldBe false
        viewModel.event.first() shouldBe SnsEditEvent.NavigateUp
    }

    it("저장에 성공하면 나간다") {
        val repository = mockk<UserConfigRepository> {
            coEvery { saveSns(any()) } returns Result.success(Unit)
        }
        val viewModel = createViewModel(repository)

        viewModel.onAction(SnsEditAction.ChangeYoutubeUsername("boolti"))
        viewModel.onAction(SnsEditAction.Save)

        viewModel.event.first() shouldBe SnsEditEvent.NavigateUp
    }

    it("저장하면 인스타그램·유튜브 계정을 각 타입에 맞게 담아 보낸다") {
        val snsList = slot<List<Sns>>()
        val repository = mockk<UserConfigRepository> {
            coEvery { saveSns(capture(snsList)) } returns Result.success(Unit)
        }
        val viewModel = createViewModel(repository)

        viewModel.onAction(SnsEditAction.ChangeYoutubeUsername("boolti_tv"))
        viewModel.onAction(SnsEditAction.Save)

        snsList.captured.associate { it.type to it.username } shouldBe mapOf(
            Sns.SnsType.INSTAGRAM to "boolti",
            Sns.SnsType.YOUTUBE to "boolti_tv",
        )
    }
})
