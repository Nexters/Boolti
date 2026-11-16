package com.nexters.boolti.data.repository

import com.nexters.boolti.common.tracker.AppTracker
import com.nexters.boolti.data.datasource.AuthDataSource
import com.nexters.boolti.data.datasource.TokenDataSource
import com.nexters.boolti.data.datasource.UserDataSource
import com.nexters.boolti.data.network.response.LoginResponse
import com.nexters.boolti.data.network.response.UserResponse
import com.nexters.boolti.domain.request.LoginRequest
import com.nexters.boolti.domain.request.SignoutRequest
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.runs
import io.mockk.unmockkObject
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf

class AuthRepositoryImplTest : DescribeSpec({

    beforeTest {
        mockkObject(AppTracker)
        every { AppTracker.identify(properties = any(), userId = any(), withLogcat = any()) } just runs
        every { AppTracker.reset(any()) } just runs
    }
    afterTest { unmockkObject(AppTracker) }

    fun createRepository(
        cached: UserResponse?,
        server: UserResponse?,
        login: LoginResponse? = null,
        tokenDataSource: TokenDataSource = mockk(),
        signoutError: Throwable? = null,
    ): AuthRepositoryImpl {
        val authDataSource = mockk<AuthDataSource> {
            every { user } returns flowOf(cached)
            every { loggedIn } returns flowOf(true)
            coEvery { updateUser(any()) } just runs
            coEvery { localLogout() } just runs
            if (login != null) coEvery { login(any()) } returns Result.success(login)
        }
        val userDataSource = mockk<UserDataSource> {
            coEvery { getUser() } returns server
            if (signoutError == null) coEvery { signout(any()) } just runs else coEvery { signout(any()) } throws signoutError
        }
        return AuthRepositoryImpl(
            authDataSource = authDataSource,
            tokenDataSource = tokenDataSource,
            authTokenDataSource = mockk(),
            signUpDataSource = mockk(),
            userDateSource = userDataSource,
            deviceTokenDataSource = mockk(),
        )
    }

    describe("내 정보 조회") {
        it("캐시에 있으면 캐시 값을 준다") {
            val repository = createRepository(cached = UserResponse(id = "cached"), server = UserResponse(id = "server"))

            repository.getUser().getOrNull()?.id shouldBe "cached"
        }

        it("캐시가 비어 있으면 서버 값을 준다") {
            val repository = createRepository(cached = null, server = UserResponse(id = "server"))

            repository.getUser().getOrNull()?.id shouldBe "server"
        }

        it("forceRefresh면 캐시가 있어도 서버 값을 준다") {
            val repository = createRepository(cached = UserResponse(id = "cached"), server = UserResponse(id = "server"))

            repository.getUser(forceRefresh = true).getOrNull()?.id shouldBe "server"
        }

        it("서버에서도 못 받으면 예외를 던지지 않고 실패 결과를 준다") {
            val repository = createRepository(cached = null, server = null)

            repository.getUser().exceptionOrNull().shouldBeInstanceOf<IllegalStateException>()
        }
    }

    describe("카카오 로그인") {
        it("가입이 필요해 토큰이 없으면 토큰을 저장하지 않고 가입 필요 상태를 준다") {
            val tokenDataSource = mockk<TokenDataSource>()
            val repository = createRepository(
                cached = null,
                server = null,
                login = LoginResponse(signUpRequired = true, accessToken = null, refreshToken = null),
                tokenDataSource = tokenDataSource,
            )

            repository.kakaoLogin(LoginRequest("kakao")).getOrNull()?.signUpRequired shouldBe true
            coVerify(exactly = 0) { tokenDataSource.saveTokens(any(), any()) }
        }
    }

    describe("탈퇴") {
        it("성공하면 트래커의 사용자 식별을 지운다") {
            val repository = createRepository(cached = null, server = null)

            repository.signout(SignoutRequest("reason")).isSuccess shouldBe true
            verify(exactly = 1) { AppTracker.reset(any()) }
        }

        it("실패하면 트래커의 사용자 식별을 지우지 않는다") {
            val repository = createRepository(cached = null, server = null, signoutError = IllegalStateException())

            repository.signout(SignoutRequest("reason")).isFailure shouldBe true
            verify(exactly = 0) { AppTracker.reset(any()) }
        }
    }

})
