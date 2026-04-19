package com.nexters.boolti.presentation.screen.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexters.boolti.common.tracker.AppTracker
import com.nexters.boolti.common.tracker.event.click
import com.nexters.boolti.common.tracker.event.search
import com.nexters.boolti.common.tracker.event.view
import com.nexters.boolti.common.tracker.field.Chip
import com.nexters.boolti.common.tracker.field.Role
import com.nexters.boolti.common.tracker.field.Screen
import com.nexters.boolti.common.tracker.field.Search
import com.nexters.boolti.domain.model.Show
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BtChip
import com.nexters.boolti.presentation.component.BtCircularProgressIndicator
import com.nexters.boolti.presentation.component.BtSearchBar
import com.nexters.boolti.presentation.component.ShowItem
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.Grey05
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.Grey85
import com.nexters.boolti.presentation.theme.Orange01
import com.nexters.boolti.presentation.theme.marginHorizontal
import java.time.LocalDate
import java.time.LocalDateTime

@Composable
fun SearchScreen(
    navigateToRecentSearch: () -> Unit,
    navigateToSearchDetail: (keyword: String) -> Unit,
    navigateToShowDetail: (id: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        AppTracker.view(
            screen = Screen.Search,
        )
    }

    LaunchedEffect(Unit) {
        viewModel.fetchNewShowsAndRisingKeywords()
    }

    SearchScreen(
        loading = uiState.loading,
        newShows = uiState.newShows,
        risingKeywords = uiState.risingKeywords,
        risingKeywordsTime = uiState.risingKeywordsTime,
        onClickSearchBar = navigateToRecentSearch,
        onSearch = navigateToSearchDetail,
        onClickShow = navigateToShowDetail,
        onClickVenueFinder = {
            // TODO: 공연장 찾기 화면 네비게이션 연결
        },
        recentSearchKeywords = uiState.searchHistory,
        deleteSearchHistory = { keyword ->
            viewModel.onIntent(SearchIntent.DeleteSearchHistory(keyword))
        },
        onClickClearButton = {
            viewModel.onIntent(SearchIntent.ShowClearHistoriesDialog)
        },
        clearSearchHistories = {
            viewModel.onIntent(SearchIntent.ClearSearchHistories)
        },
        showClearHistoriesDialog = uiState.showClearHistoriesDialog,
        dismissClearHistoriesDialog = {
            viewModel.onIntent(SearchIntent.DismissClearHistoriesDialog)
        },
        modifier = modifier,
    )
}

@Composable
private fun SearchScreen(
    loading: Boolean,
    newShows: List<Show>,
    risingKeywords: List<String>,
    risingKeywordsTime: String,
    onClickSearchBar: () -> Unit,
    onSearch: (String) -> Unit,
    onClickShow: (id: String) -> Unit,
    onClickVenueFinder: () -> Unit,
    recentSearchKeywords: List<String>,
    deleteSearchHistory: (String) -> Unit,
    onClickClearButton: () -> Unit,
    clearSearchHistories: () -> Unit,
    showClearHistoriesDialog: Boolean,
    dismissClearHistoriesDialog: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(top = 72.dp),
        ) {
            VenueFinderBanner(
                onClick = onClickVenueFinder,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = marginHorizontal)
                    .padding(top = 48.dp, bottom = 24.dp),
            )

            if (recentSearchKeywords.isNotEmpty()) {
                SearchHistorySection(
                    recentSearchKeywords = recentSearchKeywords,
                    onClickKeyword = { keyword ->
                        AppTracker.click(
                            screen = Screen.Search,
                            objectRole = Role.Chip,
                            objectValue = "RecentKeyword",
                            properties = mapOf(
                                "keyword" to keyword,
                            ),
                        )
                        AppTracker.search(
                            screen = Screen.Search,
                            keyword = keyword,
                            properties = mapOf(
                                "search_source" to "Recent",
                            ),
                        )
                        onSearch(keyword)
                    },
                    deleteSearchHistory = deleteSearchHistory,
                    onClickClearButton = onClickClearButton,
                )
            }

            NewShowsSection(
                newShows = newShows,
                onClickShow = onClickShow,
            )

            RisingKeywordsSection(
                risingKeywords = risingKeywords,
                risingKeywordsTime = risingKeywordsTime,
                onClickKeyword = { keyword ->
                    AppTracker.click(
                        screen = Screen.Search,
                        objectRole = Role.Chip,
                        objectValue = "TrendingKeyword",
                        properties = mapOf(
                            "keyword" to keyword,
                        ),
                    )
                    AppTracker.search(
                        screen = Screen.Search,
                        keyword = keyword,
                        properties = mapOf(
                            "search_source" to "Trending",
                        ),
                    )
                    onSearch(keyword)
                },
            )
        }

        StickySearchBarWithGradient(
            onClick = onClickSearchBar,
            modifier = Modifier.align(Alignment.TopStart),
        )

        if (loading) {
            BtCircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
            )
        }

        if (showClearHistoriesDialog) {
            ClearSearchHistoriesDialog(
                onClickClear = clearSearchHistories,
                onDismiss = dismissClearHistoriesDialog,
            )
        }
    }
}

@Composable
private fun VenueFinderBanner(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Orange01)
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.search_venue_finder_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.75f),
            )
            Row(
                modifier = Modifier.padding(top = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = stringResource(R.string.search_venue_finder_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
                BetaBadge()
            }
        }
        Icon(
            modifier = Modifier.size(24.dp),
            painter = painterResource(R.drawable.ic_arrow_right),
            contentDescription = null,
            tint = Color.White,
        )
    }
}

@Composable
private fun BetaBadge(
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier
            .clip(RoundedCornerShape(100.dp))
            .background(Color.White)
            .padding(horizontal = 6.dp)
            .padding(bottom = 1.dp),
        text = stringResource(R.string.search_venue_finder_beta),
        style = MaterialTheme.typography.labelMedium.copy(
            fontSize = 10.sp,
            lineHeight = 18.sp,
        ),
        fontWeight = FontWeight.Bold,
        color = Orange01,
    )
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

@Composable
private fun StickySearchBarWithGradient(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = MaterialTheme.colorScheme.background

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundColor),
        ) {
            SearchBar(
                modifier = Modifier.padding(
                    vertical = 12.dp,
                    horizontal = marginHorizontal
                ),
                onClick = onClick,
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            backgroundColor,
                            backgroundColor.copy(alpha = 0f),
                        ),
                    ),
                ),
        )
    }
}

@Composable
private fun SearchHistorySection(
    recentSearchKeywords: List<String>,
    onClickKeyword: (String) -> Unit,
    deleteSearchHistory: (String) -> Unit,
    onClickClearButton: () -> Unit,
) {
    val scrollState = rememberScrollState()

    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier
                .padding(start = marginHorizontal)
                .weight(1f),
            text = stringResource(R.string.search_recent_keyword_label),
            style = MaterialTheme.typography.titleLarge,
            color = Grey05,
        )

        if (recentSearchKeywords.size >= 2) {
            Text(
                modifier = Modifier
                    .padding(horizontal = marginHorizontal / 2)
                    .clip(RoundedCornerShape(4.dp))
                    .clickable(onClick = onClickClearButton)
                    .padding(vertical = 4.dp)
                    .padding(horizontal = marginHorizontal / 2),
                text = stringResource(R.string.btn_delete_all),
                color = Grey50,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
    Spacer(Modifier.height(16.dp))

    Row(
        modifier = Modifier.horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        recentSearchKeywords.forEachIndexed { index, keyword ->
            BtChip(
                label = keyword,
                onClick = { onClickKeyword(keyword) },
                onClickClose = { deleteSearchHistory(keyword) },
                modifier = Modifier.padding(
                    start = if (index == 0) marginHorizontal else 0.dp,
                    end = if (index == recentSearchKeywords.lastIndex) marginHorizontal else 0.dp,
                ),
            )
        }
    }

    Divider(modifier = Modifier.padding(vertical = 24.dp))
}

@Composable
private fun NewShowsSection(
    newShows: List<Show>,
    onClickShow: (id: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = buildAnnotatedString {
            withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                append("NEW!")
            }
            append(" ")
            append(stringResource(R.string.search_new_shows_label))
        },
        fontWeight = FontWeight.SemiBold,
        style = MaterialTheme.typography.titleLarge,
        color = Grey05,
        modifier = modifier.padding(horizontal = marginHorizontal),
    )

    newShows.forEachIndexed { index, show ->
        ShowItem(
            show = show,
            onClick = { onClickShow(show.id) },
            showNameStyle = MaterialTheme.typography.titleMedium,
            showDateStyle = MaterialTheme.typography.bodySmall.copy(
                color = Grey50,
            ),
            backgroundColor = MaterialTheme.colorScheme.background,
            contentPadding = PaddingValues(vertical = 10.dp, horizontal = marginHorizontal),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = if (index == 0) 6.dp else 0.dp),
        )
    }

    Divider(modifier = Modifier.padding(vertical = 24.dp))
}

@Composable
private fun RisingKeywordsSection(
    risingKeywords: List<String>,
    risingKeywordsTime: String,
    onClickKeyword: (String) -> Unit,
) {
    Row(
        modifier = Modifier.padding(horizontal = marginHorizontal),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.search_rising_keywords_label),
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.titleLarge,
            color = Grey05,
        )

        if (risingKeywordsTime.isNotBlank()) {
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = stringResource(R.string.search_rising_keywords_time, risingKeywordsTime),
                color = Grey50,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
    Spacer(Modifier.height(12.dp))

    risingKeywords.forEachIndexed { index, keyword ->
        if (index > 0) Divider(Modifier.fillMaxWidth())

        Row(
            modifier = Modifier
                .defaultMinSize(minHeight = 48.dp)
                .fillMaxWidth()
                .padding(horizontal = marginHorizontal)
                .clickable(onClick = { onClickKeyword(keyword) })
                .padding(vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = (index + 1).toString(),
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleLarge,
                color = Grey05,
            )

            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = keyword,
                style = MaterialTheme.typography.bodyLarge,
                color = Grey30,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }

    Spacer(Modifier.height(24.dp))
}

@Composable
private fun Divider(
    modifier: Modifier = Modifier,
) {
    HorizontalDivider(
        modifier = modifier.padding(horizontal = marginHorizontal),
        color = Grey85,
        thickness = 1.dp,
    )
}

@Preview
@Composable
private fun SearchScreenPreview() {
    val shows = (1..3).map {
        Show(
            id = "showId$it",
            name = "A$it Show",
            date = LocalDateTime.now(),
            salesStartDate = LocalDate.now(),
            salesEndDate = LocalDate.now().plusDays(1),
            thumbnailImage = "",
        )
    }
    BooltiTheme {
        SearchScreen(
            loading = false,
            newShows = shows,
            risingKeywords = listOf("keyword1", "keyword2", "keyword3"),
            risingKeywordsTime = "2024.01.20 18:00",
            onClickSearchBar = {},
            onSearch = {},
            onClickShow = {},
            onClickVenueFinder = {},
            recentSearchKeywords = listOf("최근검색어1", "최근검색어2"),
            deleteSearchHistory = {},
            onClickClearButton = {},
            clearSearchHistories = {},
            showClearHistoriesDialog = false,
            dismissClearHistoriesDialog = {},
        )
    }
}

@Preview
@Composable
private fun SearchScreenEmptyPreview() {
    BooltiTheme {
        SearchScreen(
            loading = false,
            newShows = emptyList(),
            risingKeywords = emptyList(),
            risingKeywordsTime = "2024.01.20 18:00",
            onClickSearchBar = {},
            onSearch = {},
            onClickShow = {},
            onClickVenueFinder = {},
            recentSearchKeywords = emptyList(),
            deleteSearchHistory = {},
            onClickClearButton = {},
            clearSearchHistories = {},
            showClearHistoriesDialog = false,
            dismissClearHistoriesDialog = {},
        )
    }
}
