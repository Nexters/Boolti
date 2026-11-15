package com.nexters.boolti.presentation.screen.ticketing

import androidx.lifecycle.SavedStateHandle
import com.nexters.boolti.domain.model.TicketingInfo
import com.nexters.boolti.domain.model.User
import com.nexters.boolti.domain.repository.AuthRepository
import com.nexters.boolti.domain.repository.TicketingRepository
import com.nexters.boolti.domain.usecase.GetRefundPolicyUseCase
import com.nexters.boolti.presentation.R
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class TicketingViewModelTest : DescribeSpec({

    beforeTest { Dispatchers.setMain(UnconfinedTestDispatcher()) }
    afterTest { Dispatchers.resetMain() }

    fun createViewModel(
        repository: TicketingRepository,
        authRepository: AuthRepository = mockk { coEvery { getUser(any()) } returns Result.success(User.My(id = "user")) },
    ) = TicketingViewModel(
        savedStateHandle = SavedStateHandle(mapOf("showId" to "show", "salesTicketId" to "ticket")),
        repository = repository,
        authRepository = authRepository,
        getRefundPolicyUseCase = mockk { every { this@mockk.invoke() } returns flowOf(emptyList()) },
    )

    describe("유료 티켓 예매") {
        it("주문 번호 요청 중에 다시 눌러도 요청은 한 번만 나간다") {
            val repository = mockk<TicketingRepository> {
                coEvery { getTicketingInfo(any()) } returns Result.success(TicketingInfo(totalPrice = 10_000))
                coEvery { getPreQuestions(any()) } returns Result.success(emptyList())
                coEvery { requestOrderId(any()) } coAnswers { awaitCancellation() }
            }
            val viewModel = createViewModel(repository)

            viewModel.onAction(TicketingAction.ConfirmReservation)
            viewModel.onAction(TicketingAction.ConfirmReservation)

            coVerify(exactly = 1) { repository.requestOrderId(any()) }
        }

        it("초청 코드 확인 중이어도 예매 요청은 막히지 않는다") {
            val repository = mockk<TicketingRepository> {
                coEvery { getTicketingInfo(any()) } returns Result.success(TicketingInfo(totalPrice = 10_000))
                coEvery { getPreQuestions(any()) } returns Result.success(emptyList())
                coEvery { checkInviteCode(any()) } coAnswers { awaitCancellation() }
                coEvery { requestOrderId(any()) } coAnswers { awaitCancellation() }
            }
            val viewModel = createViewModel(repository)

            viewModel.onAction(TicketingAction.CheckInviteCode)
            viewModel.onAction(TicketingAction.ConfirmReservation)

            coVerify(exactly = 1) { repository.requestOrderId(any()) }
        }

        it("사용자 정보를 끝내 못 받으면 에러 메시지를 띄운다") {
            val repository = mockk<TicketingRepository> {
                coEvery { getTicketingInfo(any()) } returns Result.success(TicketingInfo(totalPrice = 10_000))
                coEvery { getPreQuestions(any()) } returns Result.success(emptyList())
            }
            val authRepository = mockk<AuthRepository> { coEvery { getUser(any()) } returns Result.failure(IllegalStateException()) }
            val viewModel = createViewModel(repository, authRepository)

            viewModel.onAction(TicketingAction.ConfirmReservation)

            viewModel.event.first() shouldBe TicketingEvent.ShowErrorMessage(R.string.message_unknown_error)
        }
    }
})
