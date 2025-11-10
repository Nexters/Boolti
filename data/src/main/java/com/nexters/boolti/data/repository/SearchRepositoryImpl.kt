package com.nexters.boolti.data.repository

import com.nexters.boolti.data.datasource.SearchDataSource
import com.nexters.boolti.domain.model.NewShowsAndRisingKeywords
import com.nexters.boolti.domain.model.PagingData
import com.nexters.boolti.domain.model.Show
import com.nexters.boolti.domain.model.User
import com.nexters.boolti.domain.model.emptyPreviewList
import com.nexters.boolti.domain.repository.SearchRepository
import com.nexters.boolti.domain.util.suspendRunCatching
import javax.inject.Inject

internal class SearchRepositoryImpl @Inject constructor(
    private val searchDataSource: SearchDataSource,
) : SearchRepository {
    override suspend fun getNewShowsAndRisingKeywords(): Result<NewShowsAndRisingKeywords> = suspendRunCatching {
        searchDataSource.getOverview()
    }

    override suspend fun getRecommendKeyword(searchKeyword: String): Result<List<String>> {
        val recommendations = when {
            searchKeyword.contains("불티", ignoreCase = true) -> listOf("불티 밴드", "불티 콘서트", "불티 공연")
            searchKeyword.contains("재즈", ignoreCase = true) -> listOf("재즈 콘서트", "재즈 바", "재즈 밴드")
            searchKeyword.contains("클래식", ignoreCase = true) -> listOf("클래식 기타", "클래식 콘서트", "클래식 음악회")
            searchKeyword.contains("밴드", ignoreCase = true) -> listOf("밴드 공연", "밴드 콘서트", "인디 밴드")
            else -> listOf("${searchKeyword} 공연", "${searchKeyword} 콘서트", "${searchKeyword} 티켓")
        }

        return Result.success(recommendations)
    }

    override suspend fun searchProfiles(keyword: String): Result<List<User.Others>> {
        val allProfiles = listOf(
            User.Others(
                nickname = "불티밴드",
                photo = "https://picsum.photos/100/100",
                userCode = "boolti_band",
                introduction = "인디 밴드 불티입니다. 열정적인 공연을 선보입니다!",
                sns = emptyList(),
                link = emptyPreviewList(),
                performedShow = emptyPreviewList(),
                upcomingShow = emptyPreviewList(),
                video = emptyPreviewList(),
            ),
            User.Others(
                nickname = "재즈퀸",
                photo = "https://picsum.photos/100/101",
                userCode = "jazz_queen",
                introduction = "재즈 보컬리스트입니다. 감미로운 목소리로 여러분을 찾아갑니다.",
                sns = emptyList(),
                link = emptyPreviewList(),
                performedShow = emptyPreviewList(),
                upcomingShow = emptyPreviewList(),
                video = emptyPreviewList(),
            ),
            User.Others(
                nickname = "클래식기타리스트",
                photo = "https://picsum.photos/100/102",
                userCode = "classical_guitarist",
                introduction = "클래식 기타 연주자입니다. 아름다운 선율을 선사합니다.",
                sns = emptyList(),
                link = emptyPreviewList(),
                performedShow = emptyPreviewList(),
                upcomingShow = emptyPreviewList(),
                video = emptyPreviewList(),
            ),
            User.Others(
                nickname = "록스타",
                photo = "https://picsum.photos/100/103",
                userCode = "rockstar",
                introduction = "록 음악을 사랑하는 뮤지션입니다.",
                sns = emptyList(),
                link = emptyPreviewList(),
                performedShow = emptyPreviewList(),
                upcomingShow = emptyPreviewList(),
                video = emptyPreviewList(),
            ),
        )

        val filteredProfiles = if (keyword.isBlank()) {
            allProfiles
        } else {
            allProfiles.filter {
                it.nickname.contains(keyword, ignoreCase = true) ||
                        it.userCode.contains(keyword, ignoreCase = true) ||
                        it.introduction.contains(keyword, ignoreCase = true)
            }
        }

        return Result.success(filteredProfiles)
    }

    override suspend fun searchShows(
        keyword: String,
        page: Int
    ): Result<PagingData<Show>> = suspendRunCatching {
        searchDataSource.getShows(keyword, page, 10)
    }
}
