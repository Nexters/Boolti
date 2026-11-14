package com.nexters.boolti.presentation.screen.profileedit.profile

import com.nexters.boolti.domain.model.PreviewList
import com.nexters.boolti.domain.model.ToggleResult
import com.nexters.boolti.domain.model.User
import com.nexters.boolti.domain.repository.AuthRepository
import com.nexters.boolti.domain.repository.FileRepository
import com.nexters.boolti.domain.repository.UserConfigRepository
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileEditViewModelTest : DescribeSpec({

    beforeTest { Dispatchers.setMain(UnconfinedTestDispatcher()) }
    afterTest { Dispatchers.resetMain() }

    val user = User.My(
        id = "user",
        userCode = "boolti",
        photo = "https://old.png",
        upcomingShow = PreviewList(totalSize = 2, hasMoreItems = false, previewItems = emptyList(), isVisible = true),
    )

    fun createViewModel(
        userConfigRepository: UserConfigRepository = mockk(),
        fileRepository: FileRepository = mockk(),
        cachedUser: User.My = user,
    ) = ProfileEditViewModel(
        userConfigRepository = userConfigRepository,
        authRepository = mockk<AuthRepository> { every { this@mockk.cachedUser } returns flowOf(cachedUser) },
        fileRepository = fileRepository,
    )

    describe("썸네일 변경") {
        it("업로드하는 동안 고른 이미지를 보여준다") {
            val uploadUrl = CompletableDeferred<Result<String>>()
            val fileRepository = mockk<FileRepository> {
                coEvery { requestUrlForUpload("content://new") } coAnswers { uploadUrl.await() }
            }
            val viewModel = createViewModel(fileRepository = fileRepository)

            viewModel.onAction(ProfileEditAction.SelectThumbnail("content://new"))

            viewModel.uiState.value.uploading shouldBe true
            viewModel.uiState.value.displayedThumbnail shouldBe "content://new"
        }

        it("업로드한 주소로 썸네일을 저장한다") {
            val userConfigRepository = mockk<UserConfigRepository> {
                coEvery { saveThumbnail("https://new.png") } returns Result.success(Unit)
            }
            val fileRepository = mockk<FileRepository> {
                coEvery { requestUrlForUpload("content://new") } returns Result.success("https://new.png")
            }
            val viewModel = createViewModel(userConfigRepository, fileRepository)

            viewModel.onAction(ProfileEditAction.SelectThumbnail("content://new"))

            coVerify { userConfigRepository.saveThumbnail("https://new.png") }
            viewModel.uiState.value.uploading shouldBe false
        }

        it("업로드에 실패하면 저장하지 않고 원래 썸네일로 돌아가며 에러를 알린다") {
            val userConfigRepository = mockk<UserConfigRepository>()
            val fileRepository = mockk<FileRepository> {
                coEvery { requestUrlForUpload(any()) } returns Result.failure(RuntimeException())
            }
            val viewModel = createViewModel(userConfigRepository, fileRepository)

            viewModel.onAction(ProfileEditAction.SelectThumbnail("content://new"))

            viewModel.event.first() shouldBe ProfileEditEvent.ShowUnknownError
            viewModel.uiState.value.displayedThumbnail shouldBe "https://old.png"
            coVerify(exactly = 0) { userConfigRepository.saveThumbnail(any()) }
        }

        it("저장에 실패하면 원래 썸네일로 돌아가며 에러를 알린다") {
            val userConfigRepository = mockk<UserConfigRepository> {
                coEvery { saveThumbnail(any()) } returns Result.failure(RuntimeException())
            }
            val fileRepository = mockk<FileRepository> {
                coEvery { requestUrlForUpload(any()) } returns Result.success("https://new.png")
            }
            val viewModel = createViewModel(userConfigRepository, fileRepository)

            viewModel.onAction(ProfileEditAction.SelectThumbnail("content://new"))

            viewModel.event.first() shouldBe ProfileEditEvent.ShowUnknownError
            viewModel.uiState.value.displayedThumbnail shouldBe "https://old.png"
        }
    }

    describe("공연 공개 토글") {
        it("현재 공개 여부를 반대로 바꿔 요청한다") {
            val userConfigRepository = mockk<UserConfigRepository> {
                coEvery { setUpcomingShowVisible(false) } returns Result.success(ToggleResult(false))
            }
            val viewModel = createViewModel(userConfigRepository)

            viewModel.onAction(ProfileEditAction.ToggleUpcomingShows)

            coVerify { userConfigRepository.setUpcomingShowVisible(false) }
        }

        it("요청에 실패하면 에러를 알린다") {
            val userConfigRepository = mockk<UserConfigRepository> {
                coEvery { setUpcomingShowVisible(any()) } returns Result.failure(RuntimeException())
            }
            val viewModel = createViewModel(userConfigRepository)

            viewModel.onAction(ProfileEditAction.ToggleUpcomingShows)

            viewModel.event.first() shouldBe ProfileEditEvent.ShowUnknownError
        }
    }

    describe("하위 화면 이동") {
        it("영상·링크 편집으로 갈 때 내 유저 코드를 넘긴다") {
            val viewModel = createViewModel()

            viewModel.onAction(ProfileEditAction.ClickVideo)
            viewModel.event.first() shouldBe ProfileEditEvent.NavigateToVideoEdit("boolti")

            viewModel.onAction(ProfileEditAction.ClickLink)
            viewModel.event.first() shouldBe ProfileEditEvent.NavigateToLinkEdit("boolti")
        }
    }
})
