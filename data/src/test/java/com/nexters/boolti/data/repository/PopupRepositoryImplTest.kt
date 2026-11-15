package com.nexters.boolti.data.repository

import com.nexters.boolti.data.datasource.PopupDataSource
import com.nexters.boolti.data.network.response.PopupResponse
import com.nexters.boolti.data.network.response.PopupType
import com.nexters.boolti.domain.model.Popup
import com.nexters.boolti.domain.model.PopupResult
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf

class PopupRepositoryImplTest : DescribeSpec({
    val dataSource = mockk<PopupDataSource>()
    val repository = PopupRepositoryImpl(dataSource)

    fun response(id: String, type: PopupType) = PopupResponse(
        id = id,
        type = type,
        eventUrl = null,
        view = "HOME",
        noticeTitle = "공지",
        description = "내용",
        startDate = "2026-11-15T00:00:00",
        endDate = "2026-11-16T00:00:00",
    )

    it("팝업이 없으면 None을 반환한다") {
        coEvery { dataSource.getPopup("HOME") } returns null

        repository.getPopup("HOME") shouldBe PopupResult.None
    }

    it("공지는 숨김 여부와 상관없이 Show를 반환한다") {
        coEvery { dataSource.getPopup("HOME") } returns response("1", PopupType.NOTICE)
        every { dataSource.shouldShowEvent("1") } returns flowOf(false)

        repository.getPopup("HOME")
            .shouldBeInstanceOf<PopupResult.Show>()
            .popup.shouldBeInstanceOf<Popup.Notice>()
    }

    it("오늘 숨기지 않은 이벤트는 Show를 반환한다") {
        coEvery { dataSource.getPopup("HOME") } returns response("2", PopupType.EVENT)
        every { dataSource.shouldShowEvent("2") } returns flowOf(true)

        repository.getPopup("HOME")
            .shouldBeInstanceOf<PopupResult.Show>()
            .popup.id shouldBe "2"
    }

    it("오늘 하루 숨긴 이벤트는 Hidden을 반환한다") {
        coEvery { dataSource.getPopup("HOME") } returns response("2", PopupType.EVENT)
        every { dataSource.shouldShowEvent("2") } returns flowOf(false)

        repository.getPopup("HOME")
            .shouldBeInstanceOf<PopupResult.Hidden>()
            .popup.id shouldBe "2"
    }
})
