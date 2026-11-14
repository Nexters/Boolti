package com.nexters.boolti.data.datasource

import com.nexters.boolti.data.network.response.SignUpResponse
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import java.io.IOException

class AuthTokenDataSourceTest : BehaviorSpec() {
    private val authDataSource = mockk<AuthDataSource>()
    private val tokenDataSource = mockk<TokenDataSource>()

    init {
        beforeTest {
            clearMocks(authDataSource, tokenDataSource)
            // 저장소 흉내: saveTokens로 바꾼 값을 getAccessToken이 돌려준다
            var saved = "old"
            coEvery { tokenDataSource.getAccessToken() } answers { saved }
            coEvery { tokenDataSource.saveTokens(any(), any()) } answers { saved = firstArg() }
            coEvery { authDataSource.logout() } returns Result.success(Unit)
        }

        Given("같은 만료 토큰으로 두 요청이 동시에 401을 받으면") {
            Then("refresh는 한 번만 호출되고 둘 다 새 토큰을 받는다") {
                coEvery { authDataSource.refresh() } coAnswers {
                    delay(100)
                    Result.success(SignUpResponse(accessToken = "new", refreshToken = "newRefresh"))
                }
                runTest {
                    val dataSource = AuthTokenDataSource(authDataSource, tokenDataSource)
                    val tokens = List(2) { async { dataSource.getNewAccessToken("old") } }.awaitAll()

                    tokens shouldBe listOf("new", "new")
                    coVerify(exactly = 1) { authDataSource.refresh() }
                }
            }
        }

        Given("refresh가 네트워크 오류로 실패하면") {
            Then("null을 반환하고 로그아웃하지 않는다") {
                coEvery { authDataSource.refresh() } returns Result.failure(IOException())
                runTest {
                    AuthTokenDataSource(authDataSource, tokenDataSource).getNewAccessToken("old") shouldBe null
                    coVerify(exactly = 0) { authDataSource.logout() }
                }
            }
        }
    }
}
