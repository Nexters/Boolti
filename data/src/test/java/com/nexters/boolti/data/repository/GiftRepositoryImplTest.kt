package com.nexters.boolti.data.repository

import com.nexters.boolti.data.datasource.GiftDataSource
import com.nexters.boolti.domain.exception.TicketingErrorType
import com.nexters.boolti.domain.exception.TicketingException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.single
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

class GiftRepositoryImplTest : BehaviorSpec() {
    private val dataSource = mockk<GiftDataSource>()
    private val repository = GiftRepositoryImpl(dataSource)

    init {
        Given("선물 결제 승인") {
            When("서버가 매진 에러를 주면") {
                coEvery { dataSource.approveGiftPayment(any()) } returns Response.error(
                    400,
                    """{"errorTraceId":"t","type":"NO_REMAINING_QUANTITY","detail":"d"}""".toResponseBody(),
                )

                Then("NoRemainingQuantity TicketingException을 던진다") {
                    shouldThrow<TicketingException> {
                        repository.approveGiftPayment(mockk()).single()
                    }.errorType shouldBe TicketingErrorType.NoRemainingQuantity
                }
            }

            When("성공 응답의 body가 비어 있으면") {
                coEvery { dataSource.approveGiftPayment(any()) } returns Response.success(null)

                Then("Unknown TicketingException을 던진다") {
                    shouldThrow<TicketingException> {
                        repository.approveGiftPayment(mockk()).single()
                    }.errorType shouldBe TicketingErrorType.Unknown
                }
            }
        }
    }
}
