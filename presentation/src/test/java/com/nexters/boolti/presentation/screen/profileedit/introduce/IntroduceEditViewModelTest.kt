package com.nexters.boolti.presentation.screen.profileedit.introduce

import com.nexters.boolti.domain.model.User
import com.nexters.boolti.domain.repository.UserConfigRepository
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class IntroduceEditViewModelTest : DescribeSpec({

    beforeTest { Dispatchers.setMain(UnconfinedTestDispatcher()) }
    afterTest { Dispatchers.resetMain() }

    fun createViewModel(repository: UserConfigRepository = mockk()) = IntroduceEditViewModel(
        getCachedUserUseCase = mockk { every { this@mockk.invoke() } returns User.My(id = "user", introduction = "안녕") },
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
})
