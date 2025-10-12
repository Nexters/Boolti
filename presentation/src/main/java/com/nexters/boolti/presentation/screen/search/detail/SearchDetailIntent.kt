package com.nexters.boolti.presentation.screen.search.detail

sealed interface SearchDetailIntent {
    data class KeywordChanged(val keyword: String) : SearchDetailIntent
    data class Search(val keyword: String) : SearchDetailIntent
    data class ChangeTabIndex(val index: Int) : SearchDetailIntent
}
