package com.nexters.boolti.presentation.screen.search.recent

import androidx.compose.runtime.Stable
import com.nexters.boolti.presentation.extension.is한글자모

@Stable
data class RecentSearchUiState(
    val keyword: String,
    val recommendedKeywords: List<String>,
    val recentSearchKeywords: List<String>,
    val showClearDialog: Boolean,
) {
    val showClearButton: Boolean = recentSearchKeywords.size >= 2

    // 검색 효율을 높이기 위해 마지막 한 글자가 한글자모인 경우 마지막 글자를 제외하고 검색
    val searchKeyword: String
        get() {
            val trimmed = keyword.trim()
            if (trimmed.isEmpty()) return ""

            // 1글자이거나, 마지막 2글자가 모두 한글 자모인 경우 그대로 반환
            if (
                trimmed.length == 1 ||
                (trimmed.length >= 2 && trimmed.takeLast(2).all { it.is한글자모() })
            ) {
                return trimmed
            }

            // 마지막 글자만 한글 자모인 경우 제거
            return if (trimmed.last().is한글자모()) trimmed.dropLast(1) else trimmed
        }

    companion object {
        val Default = RecentSearchUiState(
            keyword = "",
            recommendedKeywords = emptyList(),
            recentSearchKeywords = emptyList(),
            showClearDialog = false,
        )

        val Mock = RecentSearchUiState(
            keyword = "",
            recommendedKeywords = listOf("바나나차차", "바다", "바다의 왕자", "바람", "바라보다", "가바나", "하바나"),
            recentSearchKeywords = emptyList(),
            showClearDialog = false,
        )
    }
}
