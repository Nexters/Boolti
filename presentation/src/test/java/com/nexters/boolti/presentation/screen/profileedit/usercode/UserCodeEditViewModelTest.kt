package com.nexters.boolti.presentation.screen.profileedit.usercode

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.nexters.boolti.domain.repository.UserConfigRepository
import com.nexters.boolti.presentation.screen.navigation.ProfileRoute
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class UserCodeEditViewModelTest : DescribeSpec({

    lateinit var dispatcher: TestDispatcher
    beforeTest {
        dispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(dispatcher)
    }
    afterTest { Dispatchers.resetMain() }

    // toRoute()는 안에서 Bundle을 써서 JVM 테스트에서 돌지 않는다 (b/349807172)
    beforeSpec { mockkStatic("androidx.navigation.SavedStateHandleKt") }
    afterSpec { unmockkStatic("androidx.navigation.SavedStateHandleKt") }

    fun createViewModel(repository: UserConfigRepository = mockk()) = UserCodeEditViewModel(
        savedStateHandle = SavedStateHandle().also {
            every { it.toRoute<ProfileRoute.ProfileUserCodeEdit>() } returns ProfileRoute.ProfileUserCodeEdit(userCode = "Boolti")
        },
        userConfigRepository = repository,
    )

    describe("중복 확인") {
        it("입력을 멈추면 마지막 코드만 중복 확인하고, 중복이면 에러를 보여준다") {
            val repository = mockk<UserConfigRepository> {
                coEvery { checkUserCodeDuplicated("taken") } returns Result.success(true)
            }
            val viewModel = createViewModel(repository)

            viewModel.onAction(UserCodeEditAction.ChangeUserCode("take"))
            viewModel.onAction(UserCodeEditAction.ChangeUserCode("Taken"))
            dispatcher.scheduler.advanceUntilIdle()

            coVerify(exactly = 1) { repository.checkUserCodeDuplicated(any()) }
            viewModel.uiState.value.userCodeError shouldBe UserCodeError.Duplicated
        }

        it("중복 확인에 실패해도 확인 중 상태가 풀려 저장할 수 있다") {
            val repository = mockk<UserConfigRepository> {
                coEvery { checkUserCodeDuplicated(any()) } returns Result.failure(RuntimeException())
            }
            val viewModel = createViewModel(repository)

            viewModel.onAction(UserCodeEditAction.ChangeUserCode("newcode"))
            dispatcher.scheduler.advanceUntilIdle()

            viewModel.uiState.value.checkingDuplicated shouldBe false
            viewModel.uiState.value.saveEnabled shouldBe true
        }

        it("원래 코드로 돌아오면 중복 확인을 하지 않는다") {
            val repository = mockk<UserConfigRepository>()
            val viewModel = createViewModel(repository)

            viewModel.onAction(UserCodeEditAction.ChangeUserCode("BOOLTI"))
            dispatcher.scheduler.advanceUntilIdle()

            coVerify(exactly = 0) { repository.checkUserCodeDuplicated(any()) }
            viewModel.uiState.value.saveEnabled shouldBe true
        }
    }

    describe("뒤로 가기") {
        it("저장할 수 있는 코드로 바꿨으면 나가기 확인 팝업을 띄운다") {
            val repository = mockk<UserConfigRepository> {
                coEvery { checkUserCodeDuplicated(any()) } returns Result.success(false)
            }
            val viewModel = createViewModel(repository)

            viewModel.onAction(UserCodeEditAction.ChangeUserCode("newcode"))
            viewModel.onAction(UserCodeEditAction.ClickBack)

            viewModel.uiState.value.showExitAlertDialog shouldBe true
        }

        it("형식이 틀린 코드면 팝업 없이 나간다") {
            val viewModel = createViewModel()

            viewModel.onAction(UserCodeEditAction.ChangeUserCode("ab"))
            viewModel.onAction(UserCodeEditAction.ClickBack)

            viewModel.event.first() shouldBe UserCodeEditEvent.NavigateUp
        }
    }
})
