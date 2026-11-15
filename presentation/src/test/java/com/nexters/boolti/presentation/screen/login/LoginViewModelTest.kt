package com.nexters.boolti.presentation.screen.login

import com.nexters.boolti.common.tracker.AppTracker
import com.nexters.boolti.domain.model.LoginUserState
import com.nexters.boolti.domain.repository.AuthRepository
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.runs
import io.mockk.unmockkObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest : DescribeSpec({

    beforeTest {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        mockkObject(AppTracker)
        every { AppTracker.trackEvent(any(), any(), any()) } just runs
    }
    afterTest {
        unmockkObject(AppTracker)
        Dispatchers.resetMain()
    }

    describe("카카오 idToken을 읽지 못하면") {
        val authRepository = mockk<AuthRepository> {
            coEvery { kakaoLogin(any()) } returns Result.success(LoginUserState(signUpRequired = true))
        }

        it("앱이 죽지 않고 로그인은 진행하며, 가입은 서버 요청 없이 실패한다") {
            val viewModel = LoginViewModel(authRepository)

            viewModel.loginWithKaKao(accessToken = "access", idToken = "잘못된 토큰")
            viewModel.event.first() shouldBe LoginEvent.RequireSignUp

            viewModel.signUp()
            viewModel.event.first() shouldBe LoginEvent.SignupFailed
            coVerify(exactly = 0) { authRepository.signUp(any()) }
        }
    }
})
