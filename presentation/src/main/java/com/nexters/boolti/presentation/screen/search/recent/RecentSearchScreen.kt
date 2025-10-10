package com.nexters.boolti.presentation.screen.search.recent

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BtBackAppBar
import com.nexters.boolti.presentation.component.BtSearchBar
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey70
import com.nexters.boolti.presentation.theme.marginHorizontal

@Composable
fun RecentSearchScreen(
    navigateBack: () -> Unit,
    search: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    RecentSearchScreen(
        keyword = "",
        onKeywordChanged = {},
        recentKeywords = listOf("keyword1", "keyword2", "keyword3"),
        deleteKeyword = {},
        deleteAllKeywords = {},
        showDeleteAllDialog = false,
        navigateBack = navigateBack,
        search = { search("") },
        modifier = modifier,
    )
}

@Composable
fun RecentSearchScreen(
    keyword: String,
    onKeywordChanged: (String) -> Unit,
    recentKeywords: List<String>,
    deleteKeyword: (keyword: String) -> Unit,
    deleteAllKeywords: () -> Unit,
    showDeleteAllDialog: Boolean,
    navigateBack: () -> Unit,
    search: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            BtBackAppBar(
                title = stringResource(R.string.menu_search),
                onClickBack = navigateBack,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = marginHorizontal),
        ) {
            BtSearchBar(
                keyword = keyword,
                onKeywordChanged = onKeywordChanged,
                hint = stringResource(R.string.search_search_hint),
                search = search,
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
            )

            Text(
                modifier = Modifier.padding(top = 12.dp),
                text = stringResource(R.string.search_recent_keyword),
                style = MaterialTheme.typography.bodySmall,
                color = Grey30,
            )

            Spacer(Modifier.height(4.dp))

            Text(
                modifier = Modifier.padding(top = 12.dp),
                text = stringResource(R.string.search_no_recent_keyword),
                style = MaterialTheme.typography.bodyLarge,
                color = Grey70,
            )
        }
    }
}
