package com.nexters.boolti.data.repository

import com.nexters.boolti.common.tracker.AppTracker
import com.nexters.boolti.data.datasource.AuthDataSource
import com.nexters.boolti.data.datasource.UserDataSource
import com.nexters.boolti.data.network.response.UserResponse
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.runs
import io.mockk.unmockkObject
import kotlinx.coroutines.flow.flowOf

class AuthRepositoryImplTest : DescribeSpec({

    beforeTest {
        mockkObject(AppTracker)
        every { AppTracker.identify(properties = any(), userId = any(), withLogcat = any()) } just runs
    }
    afterTest { unmockkObject(AppTracker) }

    fun createRepository(cached: UserResponse?, server: UserResponse?): AuthRepositoryImpl {
        val authDataSource = mockk<AuthDataSource> {
            every { user } returns flowOf(cached)
            every { loggedIn } returns flowOf(true)
            coEvery { updateUser(any()) } just runs
        }
        val userDataSource = mockk<UserDataSource> { coEvery { getUser() } returns server }
        return AuthRepositoryImpl(
            authDataSource = authDataSource,
            tokenDataSource = mockk(),
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
})
