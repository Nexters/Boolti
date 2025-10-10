package com.nexters.boolti.presentation.screen.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nexters.boolti.domain.model.Show
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BtSearchBar
import com.nexters.boolti.presentation.theme.marginHorizontal

@Composable
fun SearchScreen(
    navigateToRecentSearch: () -> Unit,
    navigateToSearchDetail: (keyword: String) -> Unit,
    navigateToShowDetail: (id: String) -> Unit,
    modifier: Modifier = Modifier
) {
    SearchScreen(
        shows = emptyList(),
        risingKeywords = emptyList(),
        risingKeywordsTime = "00:00",
        onClickSearchBar = navigateToRecentSearch,
        onClickRecentSearchKeyword = navigateToSearchDetail,
        onClickShow = navigateToShowDetail,
        recentSearchKeywords = linkedSetOf(),
        deleteKeyword = {},
        deleteAllKeywords = {},
        modifier = modifier,
    )
}

@Composable
private fun SearchScreen(
    shows: List<Show>,
    risingKeywords: List<String>,
    risingKeywordsTime: String,
    onClickSearchBar: () -> Unit,
    onClickRecentSearchKeyword: (keyword: String) -> Unit,
    onClickShow: (id: String) -> Unit,
    recentSearchKeywords: LinkedHashSet<String>,
    deleteKeyword: (String) -> Unit,
    deleteAllKeywords: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = marginHorizontal),
        ) {
            SearchBar(onClick = onClickSearchBar)
        }
    }
}

@Composable
private fun SearchBar(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BtSearchBar(
        modifier = modifier
            .padding(vertical = 12.dp)
            .clickable(
                interactionSource = null,
                indication = null,
                onClick = onClick,
            ),
        keyword = "",
        onKeywordChanged = {},
        enabled = false,
        hint = stringResource(R.string.search_search_hint),
        search = {},
    )
}
