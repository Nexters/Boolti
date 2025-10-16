package com.nexters.boolti.presentation.screen.search.detail

import androidx.compose.runtime.Stable
import com.nexters.boolti.domain.model.Show
import com.nexters.boolti.domain.model.User
import java.time.LocalDateTime

@Stable
data class SearchDetailUiState(
    val loading: Boolean,
    val keyword: String,
    val searchedKeyword: String,
    val shows: List<Show>,
    val profiles: List<User.Others>,
    val tabIndex: Int,
) {
    companion object {
        val Default = SearchDetailUiState(
            loading = false,
            keyword = "",
            searchedKeyword = "",
            shows = listOf(
                Show(
                    id = "1",
                    name = "2024 TOGETHER LUCKY CLUB",
                    date = LocalDateTime.now(),
                    salesEndDate = null,
                    salesStartDate = null,
                    thumbnailImage = "",
                ),
                Show(
                    id = "2",
                    name = "TUNE Project No.3 TUNE’s Halloween Party",
                    date = LocalDateTime.now(),
                    salesEndDate = null,
                    salesStartDate = null,
                    thumbnailImage = "",
                ),
                Show(
                    id = "3",
                    name = "쇼팽과 라벨",
                    date = LocalDateTime.now(),
                    salesEndDate = null,
                    salesStartDate = null,
                    thumbnailImage = "",
                ),
                Show(
                    id = "4",
                    name = "2024 TOGETHER LUCKY CLUB",
                    date = LocalDateTime.now(),
                    salesEndDate = null,
                    salesStartDate = null,
                    thumbnailImage = "",
                ),
                Show(
                    id = "5",
                    name = "TUNE Project No.3 TUNE’s Halloween Party",
                    date = LocalDateTime.now(),
                    salesEndDate = null,
                    salesStartDate = null,
                    thumbnailImage = "",
                ),
                Show(
                    id = "6",
                    name = "쇼팽과 라벨",
                    date = LocalDateTime.now(),
                    salesEndDate = null,
                    salesStartDate = null,
                    thumbnailImage = "",
                ),
            ),
            profiles = listOf(
                User.Others(
                    nickname = "아티스트 닉네임은 최대 12자",
                    photo = null,
                    userCode = "1",
                ),
                User.Others(
                    nickname = "김람지",
                    photo = null,
                    userCode = "2",
                ),
                User.Others(
                    nickname = "스튜디오 불티",
                    photo = null,
                    userCode = "3",
                ),
                User.Others(
                    nickname = "아티스트 닉네임은 최대 12자",
                    photo = null,
                    userCode = "4",
                ),
                User.Others(
                    nickname = "김람지",
                    photo = null,
                    userCode = "5",
                ),
                User.Others(
                    nickname = "스튜디오 불티",
                    photo = null,
                    userCode = "6",
                ),
            ),
            tabIndex = 0,
        )
    }
}
