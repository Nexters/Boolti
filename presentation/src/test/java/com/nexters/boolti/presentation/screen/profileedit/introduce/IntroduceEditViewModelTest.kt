package com.nexters.boolti.presentation.screen.profileedit.introduce

import com.nexters.boolti.domain.model.User
import com.nexters.boolti.domain.repository.UserConfigRepository
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class IntroduceEditViewModelTest : DescribeSpec({

    beforeTest { Dispatchers.setMain(UnconfinedTestDispatcher()) }
    afterTest { Dispatchers.resetMain() }

    fun createViewModel(
        repository: UserConfigRepository = mockk(),
        user: suspend () -> Result<User.My> = { Result.success(User.My(id = "user", introduction = "안녕")) },
    ) = IntroduceEditViewModel(
        authRepository = mockk { coEvery { getUser(any()) } coAnswers { user() } },
        userConfigRepository = repository,
    )

    it("소개를 바꾸고 뒤로 가면 나가기 확인 팝업을 띄운다") {
        val viewModel = createViewModel()

        viewModel.onAction(IntroduceEditAction.ChangeIntroduce(""))
        viewModel.onAction(IntroduceEditAction.ClickBack)

        viewModel.uiState.value.showExitAlertDialog shouldBe true
    }

    it("저장에 성공하면 나간다") {
        val repository = mockk<UserConfigRepository> {
            coEvery { saveIntroduce("반가워요") } returns Result.success("반가워요")
        }
        val viewModel = createViewModel(repository)

        viewModel.onAction(IntroduceEditAction.ChangeIntroduce("반가워요"))
        viewModel.onAction(IntroduceEditAction.Save)

        viewModel.event.first() shouldBe IntroduceEditEvent.NavigateUp
    }

    describe("내 정보 불러오기") {
        it("불러오기 전에는 입력해도 저장할 수 없다") {
            val viewModel = createViewModel(user = { awaitCancellation() })

            viewModel.onAction(IntroduceEditAction.ChangeIntroduce("새 소개"))

            viewModel.uiState.value.saveEnabled shouldBe false
        }

        it("불러오지 못하면 실패를 알린다") {
            val viewModel = createViewModel(user = { Result.failure(RuntimeException()) })

            viewModel.event.first() shouldBe IntroduceEditEvent.LoadFailed
        }
    }
})
