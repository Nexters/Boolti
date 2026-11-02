package com.nexters.boolti.presentation.screen.reservationdetail

import androidx.lifecycle.SavedStateHandle
import com.nexters.boolti.domain.model.ReservationDetail
import com.nexters.boolti.domain.model.ReservationState
import com.nexters.boolti.domain.repository.ReservationRepository
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class ReservationDetailViewModelTest : DescribeSpec({

    beforeTest { Dispatchers.setMain(UnconfinedTestDispatcher()) }
    afterTest { Dispatchers.resetMain() }

    describe("예약을 다시 불러오면") {
        it("진행 중이던 사전 질문 답변 요청은 취소된다") {
            var activeRequests = 0
            val reservation = mockk<ReservationDetail>(relaxed = true) {
                every { reservationState } returns ReservationState.RESERVED
                every { salesEndDateTime } returns LocalDateTime.MAX
            }
            val repository = mockk<ReservationRepository> {
                every { findReservationById(any()) } returns flowOf(reservation)
                every { getPreQuestionAnswers(any()) } returns flow<Nothing> {
                    activeRequests++
                    awaitCancellation()
                }.onCompletion { activeRequests-- }
            }
            val viewModel = ReservationDetailViewModel(
                savedStateHandle = SavedStateHandle(mapOf("reservationId" to "1")),
                reservationRepository = repository,
                giftRepository = mockk(),
                getRefundPolicyUseCase = mockk { every { this@mockk.invoke() } returns emptyFlow() },
            )

            viewModel.fetchReservation()
            viewModel.fetchReservation()

            verify(exactly = 2) { repository.getPreQuestionAnswers(any()) }
            activeRequests shouldBe 1
        }
    }
})
