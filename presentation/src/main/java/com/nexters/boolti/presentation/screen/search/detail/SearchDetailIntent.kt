package com.nexters.boolti.presentation.screen.search.detail

sealed interface SearchDetailIntent {
    data class ChangeTabIndex(val index: Int) : SearchDetailIntent

    data object OnShowsPageReached : SearchDetailIntent
    data object OnProfilesPageReached : SearchDetailIntent
}
