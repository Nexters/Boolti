package com.nexters.boolti.presentation.screen.search.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexters.boolti.domain.model.UserCode
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BtBackAppBar
import com.nexters.boolti.presentation.component.BtSearchBar
import com.nexters.boolti.presentation.theme.marginHorizontal

@Composable
fun SearchDetailScreen(
    navigateToShowDetail: (id: String) -> Unit,
    navigateToProfile: (userCode: UserCode) -> Unit,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SearchDetailScreen(
        keyword = uiState.keyword,
        onChangeKeyword = {},
        searchedKeyword = uiState.searchedKeyword,
        tabIndex = uiState.tabIndex,
        onChangeIndex = {},
        onClickShow = navigateToShowDetail,
        onClickProfile = navigateToProfile,
        search = {},
        navigateUp = navigateUp,
        modifier = modifier,
    )
}

@Composable
private fun SearchDetailScreen(
    keyword: String,
    onChangeKeyword: (String) -> Unit,
    searchedKeyword: String,
    tabIndex: Int,
    onChangeIndex: (Int) -> Unit,
    onClickShow: (id: String) -> Unit,
    onClickProfile: (userCode: UserCode) -> Unit,
    search: (keyword: String) -> Unit,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            BtBackAppBar(
                title = stringResource(R.string.menu_search),
                onClickBack = navigateUp,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            BtSearchBar(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .padding(horizontal = marginHorizontal),
                keyword = keyword,
                hint = stringResource(R.string.search_search_hint),
                onKeywordChanged = onChangeKeyword,
                search = { search(keyword) },
            )

            Text(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 120.dp),
                text = "${searchedKeyword}와 관련된 결과가 없어요.\n검색어를 확인해 주세요.",
                textAlign = TextAlign.Center,
            )
        }
    }
}
