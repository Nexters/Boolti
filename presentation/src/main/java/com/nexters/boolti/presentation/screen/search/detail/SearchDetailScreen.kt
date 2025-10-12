package com.nexters.boolti.presentation.screen.search.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexters.boolti.domain.model.Show
import com.nexters.boolti.domain.model.User
import com.nexters.boolti.domain.model.UserCode
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BtBackAppBar
import com.nexters.boolti.presentation.component.BtCircularProgressIndicator
import com.nexters.boolti.presentation.component.BtSearchBar
import com.nexters.boolti.presentation.component.MainButton
import com.nexters.boolti.presentation.component.MainButtonDefaults
import com.nexters.boolti.presentation.extension.ellipsis
import com.nexters.boolti.presentation.theme.Grey15
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.Grey70
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
        onChangeKeyword = {
            viewModel.onIntent(SearchDetailIntent.KeywordChanged(it))
        },
        searchedKeyword = uiState.searchedKeyword,
        loading = uiState.loading,
        shows = uiState.shows,
        profiles = uiState.profiles,
        tabIndex = uiState.tabIndex,
        onChangeIndex = {
            viewModel.onIntent(SearchDetailIntent.ChangeTabIndex(it))
        },
        onClickShow = navigateToShowDetail,
        onClickProfile = navigateToProfile,
        search = {
            viewModel.onIntent(SearchDetailIntent.Search(it))
        },
        navigateUp = navigateUp,
        modifier = modifier,
    )
}

@Composable
private fun SearchDetailScreen(
    keyword: String,
    onChangeKeyword: (String) -> Unit,
    searchedKeyword: String,
    loading: Boolean,
    shows: List<Show>,
    profiles: List<User.Others>,
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

            if (!loading && shows.isEmpty() && profiles.isEmpty()) {
                EmptyContents(
                    keyword = searchedKeyword,
                    onClickResetKeyword = navigateUp,
                )
            }
        }

        if (loading) {
            Box(
                modifier = Modifier.fillMaxSize(1f),
                contentAlignment = Alignment.Center,
            ) {
                BtCircularProgressIndicator()
            }
        }
    }
}

@Composable
private fun ColumnScope.EmptyContents(
    keyword: String,
    onClickResetKeyword: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier
            .align(Alignment.CenterHorizontally)
            .padding(top = 120.dp),
        text = buildAnnotatedString {
            withStyle(SpanStyle(color = Grey15)) {
                append("'${keyword.ellipsis(5)}'")
            }
            append(stringResource(R.string.search_no_result))
        },
        color = Grey50,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodyLarge,
        lineHeight = 24.sp,
    )

    MainButton(
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(top = 20.dp),
        label = stringResource(R.string.search_reset_searched_keyword_button),
        colors = MainButtonDefaults.buttonColors(containerColor = Grey70),
        onClick = onClickResetKeyword,
    )
}
