package com.nexters.boolti.presentation.screen.profileedit.nickname

import com.nexters.boolti.domain.model.User
import com.nexters.boolti.domain.repository.UserConfigRepository
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class NicknameEditViewModelTest : DescribeSpec({

    beforeTest { Dispatchers.setMain(UnconfinedTestDispatcher()) }
    afterTest { Dispatchers.resetMain() }

    fun createViewModel(repository: UserConfigRepository = mockk()) = NicknameEditViewModel(
        getCachedUserUseCase = mockk { every { this@mockk.invoke() } returns User.My(id = "user", nickname = "불티") },
        userConfigRepository = repository,
    )

    describe("뒤로 가기") {
        it("바꾼 게 없으면 바로 나간다") {
            val viewModel = createViewModel()

            viewModel.onAction(NicknameEditAction.ClickBack)

            viewModel.event.first() shouldBe NicknameEditEvent.NavigateUp
        }

        it("올바른 닉네임으로 바꿨으면 나가기 확인 팝업을 띄운다") {
            val viewModel = createViewModel()

            viewModel.onAction(NicknameEditAction.ChangeNickname("새닉네임"))
            viewModel.onAction(NicknameEditAction.ClickBack)

            viewModel.uiState.value.showExitAlertDialog shouldBe true
        }

        it("저장할 수 없는 닉네임이면 팝업 없이 나간다") {
            val viewModel = createViewModel()

            viewModel.onAction(NicknameEditAction.ChangeNickname(""))
            viewModel.onAction(NicknameEditAction.ClickBack)

            viewModel.event.first() shouldBe NicknameEditEvent.NavigateUp
        }
    }

    describe("저장") {
        it("바꾼 게 없으면 저장 요청 없이 나간다") {
            val repository = mockk<UserConfigRepository>()
            val viewModel = createViewModel(repository)

            viewModel.onAction(NicknameEditAction.Save)

            viewModel.event.first() shouldBe NicknameEditEvent.NavigateUp
            coVerify(exactly = 0) { repository.saveNickname(any()) }
        }

        it("저장에 성공하면 나간다") {
            val repository = mockk<UserConfigRepository> {
                coEvery { saveNickname("새닉네임") } returns Result.success("새닉네임")
            }
            val viewModel = createViewModel(repository)

            viewModel.onAction(NicknameEditAction.ChangeNickname("새닉네임"))
            viewModel.onAction(NicknameEditAction.Save)

            viewModel.event.first() shouldBe NicknameEditEvent.NavigateUp
        }

        it("저장에 실패하면 다시 저장할 수 있다") {
            val repository = mockk<UserConfigRepository> {
                coEvery { saveNickname(any()) } returns Result.failure(RuntimeException())
            }
            val viewModel = createViewModel(repository)

            viewModel.onAction(NicknameEditAction.ChangeNickname("새닉네임"))
            viewModel.onAction(NicknameEditAction.Save)

            viewModel.uiState.value.saveEnabled shouldBe true
        }
    }

    describe("나가기 확인 팝업") {
        it("나가기를 누르면 팝업을 닫고 나간다") {
            val viewModel = createViewModel()
            viewModel.onAction(NicknameEditAction.ChangeNickname("새닉네임"))
            viewModel.onAction(NicknameEditAction.ClickBack)

            viewModel.onAction(NicknameEditAction.ConfirmExit)

            viewModel.uiState.value.showExitAlertDialog shouldBe false
            viewModel.event.first() shouldBe NicknameEditEvent.NavigateUp
        }
    }
})
