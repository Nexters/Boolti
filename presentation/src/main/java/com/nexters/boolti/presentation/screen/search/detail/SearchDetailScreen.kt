package com.nexters.boolti.presentation.screen.search.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
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
import com.nexters.boolti.presentation.component.ProfileItem
import com.nexters.boolti.presentation.component.ShowItem
import com.nexters.boolti.presentation.extension.ellipsis
import com.nexters.boolti.presentation.theme.Grey05
import com.nexters.boolti.presentation.theme.Grey15
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.Grey70
import com.nexters.boolti.presentation.theme.Grey85
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

            TabContainer(
                shows = shows,
                profiles = profiles,
                keyword = keyword,
                onClickShow = onClickShow,
                onClickProfile = onClickProfile,
                tabIndex = tabIndex,
                onChangeIndex = onChangeIndex,
            )

            /*
                        if (!loading && shows.isEmpty() && profiles.isEmpty()) {
                            EmptyContents(
                                keyword = searchedKeyword,
                                onClickResetKeyword = navigateUp,
                            )
                        } else if (!loading) {
                            TabContainer(
                                shows = shows,
                                profiles = profiles,
                                onClickShow = onClickShow,
                                onClickProfile = onClickProfile,
                                tabIndex = tabIndex,
                                onChangeIndex = onChangeIndex,
                            )
                        }
            */
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
private fun EmptyContents(
    keyword: String,
    content: String,
    modifier: Modifier = Modifier,
    onClickResetKeyword: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 120.dp),
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = Grey15)) {
                    append("'${keyword.ellipsis(5)}'")
                }
                append(content)
            },
            color = Grey50,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            lineHeight = 24.sp,
        )

        onClickResetKeyword?.let {
            MainButton(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 20.dp),
                label = stringResource(R.string.search_reset_searched_keyword_button),
                colors = MainButtonDefaults.buttonColors(containerColor = Grey70),
                onClick = onClickResetKeyword,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TabContainer(
    shows: List<Show>,
    profiles: List<User.Others>,
    keyword: String,
    onClickShow: (id: String) -> Unit,
    onClickProfile: (userCode: UserCode) -> Unit,
    tabIndex: Int,
    onChangeIndex: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tabs = remember(shows.size, profiles.size) {
        listOf(
            SearchDetailTab.All,
            SearchDetailTab.Show(shows.size),
            SearchDetailTab.Artist(profiles.size),
        )
    }
    val pagerState = rememberPagerState { 3 }

    LaunchedEffect(tabIndex) {
        pagerState.animateScrollToPage(tabIndex)
    }

    LaunchedEffect(pagerState.targetPage) {
        onChangeIndex(pagerState.targetPage)
    }

    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        TabRow(
            selectedIndex = tabIndex,
            tabs = tabs.map { it.toLabel() },
            onClickTab = onChangeIndex,
            modifier = Modifier.fillMaxWidth(),
        )
        HorizontalPager(
            state = pagerState,
        ) { tabIndex ->
            Box(
                modifier = Modifier.fillMaxSize(),
            ) {
                when (tabs[tabIndex]) {
                    is SearchDetailTab.All -> Text(
                        text = "Tab #$tabIndex",
                        style = MaterialTheme.typography.displayLarge,
                    )

                    is SearchDetailTab.Show -> if (shows.isEmpty()) {
                        EmptyContents(
                            keyword = keyword,
                            content = stringResource(R.string.search_no_show_result),
                        )
                    } else {
                        ShowsTab(
                            shows = shows,
                            onClickShow = onClickShow,
                        )
                    }

                    is SearchDetailTab.Artist -> if (profiles.isEmpty()) {
                        EmptyContents(
                            keyword = keyword,
                            content = stringResource(R.string.search_no_artist_result),
                        )
                    } else {
                        ArtistTab(
                            profiles = profiles,
                            onClickProfile = onClickProfile,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TabRow(
    selectedIndex: Int,
    tabs: List<String>,
    onClickTab: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
    divider: @Composable () -> Unit = {
        HorizontalDivider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp, color = Grey85)
    },
    indicatorColor: Color = Grey15,
    tabLabelStyle: TextStyle = MaterialTheme.typography.titleMedium,
    selectedContentColor: Color = Grey05,
    unselectedContentColor: Color = Grey70,
    edgePadding: Dp = marginHorizontal,
    spaceBetween: Dp = 24.dp,
    verticalPadding: Dp = 12.dp,
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier,
    ) {
        Box(modifier = Modifier.align(Alignment.BottomStart)) {
            divider()
        }
        Row(modifier = Modifier.horizontalScroll(scrollState)) {
            val edgeMargin = (edgePadding - spaceBetween / 2)

            tabs.forEachIndexed { index, tabLabel ->
                val tabModifier = when (index) {
                    0 -> {
                        Modifier
                            .padding(start = edgeMargin.coerceAtLeast(0.dp))
                            .clickable(onClick = { onClickTab(index) }, indication = null, interactionSource = null)
                            .padding(
                                start = if (edgeMargin >= 0.dp) spaceBetween / 2 else edgePadding,
                                end = spaceBetween / 2,
                            )
                    }

                    else -> {
                        Modifier
                            .clickable(onClick = { onClickTab(index) }, indication = null, interactionSource = null)
                            .padding(horizontal = spaceBetween / 2)
                    }
                }
                Text(
                    text = tabLabel,
                    style = tabLabelStyle,
                    color = if (selectedIndex == index) selectedContentColor else unselectedContentColor,
                    modifier = tabModifier
                        .padding(vertical = verticalPadding)
                        .drawWithContent(
                            onDraw = {
                                drawContent()
                                if (index == selectedIndex) {
                                    val y = size.height + verticalPadding.roundToPx() - 1.dp.roundToPx()
                                    drawLine(
                                        color = indicatorColor,
                                        start = Offset(
                                            x = 0f,
                                            y = y,
                                        ),
                                        end = Offset(
                                            x = size.width,
                                            y = y,
                                        ),
                                    )
                                }
                            },
                        ),
                )
            }
        }
    }
}

@Composable
private fun ShowsTab(
    shows: List<Show>,
    onClickShow: (id: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        items(shows, key = { it.id }) { show ->
            ShowItem(
                show = show,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = marginHorizontal),
                showNameStyle = MaterialTheme.typography.titleLarge,
                backgroundColor = MaterialTheme.colorScheme.background,
                contentPadding = PaddingValues(0.dp),
                onClick = { onClickShow(show.id) },
            )
        }
    }
}

@Composable
private fun ArtistTab(
    profiles: List<User.Others>,
    onClickProfile: (UserCode) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        items(profiles, key = { it.userCode }) { profile ->
            ProfileItem(
                profile = profile,
                onClick = onClickProfile,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = marginHorizontal),
            )
        }
    }
}

private sealed interface SearchDetailTab {
    data object All : SearchDetailTab
    data class Show(val count: Int) : SearchDetailTab
    data class Artist(val count: Int) : SearchDetailTab
}

@Composable
private fun SearchDetailTab.toLabel(): String = when (this) {
    is SearchDetailTab.All -> stringResource(R.string.search_tab_all)
    is SearchDetailTab.Show -> stringResource(R.string.search_tab_show, count)
    is SearchDetailTab.Artist -> stringResource(R.string.search_tab_artist, count)
}
