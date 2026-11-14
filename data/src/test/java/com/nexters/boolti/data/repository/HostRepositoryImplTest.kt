package com.nexters.boolti.data.repository

import com.nexters.boolti.data.datasource.HostDataSource
import com.nexters.boolti.domain.exception.QrErrorType
import com.nexters.boolti.domain.exception.QrScanException
import com.nexters.boolti.domain.request.QrScanRequest
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.single
import retrofit2.Response

class HostRepositoryImplTest : BehaviorSpec() {
    private val dataSource = mockk<HostDataSource>()
    private val repository = HostRepositoryImpl(dataSource)
    private val request = QrScanRequest(showId = "1", entryCode = "btec-code")

    init {
        Given("입장 요청이 200으로 응답할 때") {
            When("body가 true이면") {
                coEvery { dataSource.requestEntrance(request) } returns Response.success(true)
                Then("성공(true)을 내보낸다") {
                    repository.requestEntrance(request).single() shouldBe true
                }
            }
            When("body가 false이면") {
                coEvery { dataSource.requestEntrance(request) } returns Response.success(false)
                Then("Unknown 에러를 던진다") {
                    shouldThrow<QrScanException> {
                        repository.requestEntrance(request).single()
                    }.errorType shouldBe QrErrorType.Unknown
                }
            }
        }
    }
}
