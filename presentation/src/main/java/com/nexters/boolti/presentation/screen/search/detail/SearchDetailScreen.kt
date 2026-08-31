package com.nexters.boolti.presentation.screen.search.detail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexters.boolti.common.tracker.AppTracker
import com.nexters.boolti.common.tracker.event.click
import com.nexters.boolti.common.tracker.event.view
import com.nexters.boolti.common.tracker.field.Button
import com.nexters.boolti.common.tracker.field.Item
import com.nexters.boolti.common.tracker.field.Role
import com.nexters.boolti.common.tracker.field.Screen
import com.nexters.boolti.common.tracker.field.SearchDetail
import com.nexters.boolti.common.tracker.field.Tab
import com.nexters.boolti.domain.model.Place
import com.nexters.boolti.domain.model.Show
import com.nexters.boolti.domain.model.User
import com.nexters.boolti.domain.model.UserCode
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BtBackAppBar
import com.nexters.boolti.presentation.component.BtCircularProgressIndicator
import com.nexters.boolti.presentation.component.BtSearchBar
import com.nexters.boolti.presentation.component.InfiniteScrollLazyColumn
import com.nexters.boolti.presentation.component.MainButton
import com.nexters.boolti.presentation.component.MainButtonDefaults
import com.nexters.boolti.presentation.component.PlaceItem
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
    navigateToRecentSearch: (keyword: String) -> Unit,
    navigateToShowDetail: (id: String) -> Unit,
    navigateToProfile: (userCode: UserCode) -> Unit,
    navigateToPlaceDetail: (placeId: String) -> Unit,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        AppTracker.view(
            screen = Screen.SearchDetail,
            properties = mapOf(
                "keyword" to uiState.keyword,
                "tab" to "All",
            ),
        )
    }

    BackHandler {
        if (uiState.tabIndex > 0) {
            viewModel.onIntent(SearchDetailIntent.ChangeTabIndex(0))
        } else {
            navigateUp()
        }
    }

    SearchDetailScreen(
        keyword = uiState.keyword,
        navigateToRecentSearch = { navigateToRecentSearch(uiState.keyword) },
        searchedKeyword = uiState.searchedKeyword,
        loading = uiState.loading,
        shows = uiState.shows,
        showsTotalCount = uiState.showsTotalCount,
        showsLoading = uiState.showsLoading,
        profiles = uiState.profiles,
        profilesTotalCount = uiState.profilesTotalCount,
        profilesLoading = uiState.profilesLoading,
        places = uiState.places,
        placesTotalCount = uiState.placesTotalCount,
        placesLoading = uiState.placesLoading,
        tabIndex = uiState.tabIndex,
        onChangeIndex = { index ->
            viewModel.onIntent(SearchDetailIntent.ChangeTabIndex(index))
        },
        onClickShow = navigateToShowDetail,
        onClickProfile = navigateToProfile,
        onClickPlace = navigateToPlaceDetail,
        onShowsPageReached = {
            viewModel.onIntent(SearchDetailIntent.OnShowsPageReached)
        },
        onProfilesPageReached = {
            viewModel.onIntent(SearchDetailIntent.OnProfilesPageReached)
        },
        onPlacesPageReached = {
            viewModel.onIntent(SearchDetailIntent.OnPlacesPageReached)
        },
        navigateUp = navigateUp,
        modifier = modifier,
    )
}

@Composable
private fun SearchDetailScreen(
    keyword: String,
    navigateToRecentSearch: () -> Unit,
    searchedKeyword: String,
    loading: Boolean,
    shows: List<Show>,
    showsTotalCount: Long,
    showsLoading: Boolean,
    profiles: List<User.Others>,
    profilesTotalCount: Long,
    profilesLoading: Boolean,
    places: List<Place>,
    placesTotalCount: Long,
    placesLoading: Boolean,
    tabIndex: Int,
    onChangeIndex: (Int) -> Unit,
    onClickShow: (id: String) -> Unit,
    onClickProfile: (userCode: UserCode) -> Unit,
    onClickPlace: (placeId: String) -> Unit,
    onShowsPageReached: () -> Unit,
    onProfilesPageReached: () -> Unit,
    onPlacesPageReached: () -> Unit,
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
                    .padding(horizontal = marginHorizontal)
                    .clickable(
                        interactionSource = null,
                        indication = null,
                        onClick = navigateToRecentSearch,
                    ),
                keyword = keyword,
                enabled = false,
                hint = stringResource(R.string.search_search_hint),
                onKeywordChanged = {},
                search = {},
            )

            if (!loading && shows.isEmpty() && profiles.isEmpty() && places.isEmpty()) {
                EmptyContents(
                    keyword = searchedKeyword,
                    onClickResetKeyword = navigateToRecentSearch,
                    content = stringResource(R.string.search_no_result),
                )
            } else if (!loading) {
                TabContainer(
                    shows = shows,
                    showsTotalCount = showsTotalCount,
                    showsLoading = showsLoading,
                    profiles = profiles,
                    profilesTotalCount = profilesTotalCount,
                    profilesLoading = profilesLoading,
                    places = places,
                    placesTotalCount = placesTotalCount,
                    placesLoading = placesLoading,
                    keyword = keyword,
                    onClickShow = onClickShow,
                    onClickProfile = onClickProfile,
                    onClickPlace = onClickPlace,
                    tabIndex = tabIndex,
                    onChangeIndex = onChangeIndex,
                    onShowsPageReached = onShowsPageReached,
                    onProfilesPageReached = onProfilesPageReached,
                    onPlacesPageReached = onPlacesPageReached,
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
    showsTotalCount: Long,
    showsLoading: Boolean,
    profiles: List<User.Others>,
    profilesTotalCount: Long,
    profilesLoading: Boolean,
    places: List<Place>,
    placesTotalCount: Long,
    placesLoading: Boolean,
    keyword: String,
    onClickShow: (id: String) -> Unit,
    onClickProfile: (userCode: UserCode) -> Unit,
    onClickPlace: (placeId: String) -> Unit,
    tabIndex: Int,
    onChangeIndex: (Int) -> Unit,
    onShowsPageReached: () -> Unit,
    onProfilesPageReached: () -> Unit,
    onPlacesPageReached: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tabs = remember(shows.size, profiles.size, places.size) {
        listOf(
            SearchDetailTab.All,
            SearchDetailTab.Show(showsTotalCount),
            SearchDetailTab.Artist(profilesTotalCount),
            SearchDetailTab.Place(placesTotalCount),
        )
    }
    val pagerState = rememberPagerState { tabs.size }

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
            onClickTab = { index ->
                AppTracker.click(
                    screen = Screen.SearchDetail,
                    objectRole = Role.Tab,
                    objectValue = tabs.getOrNull(index)?.trackerValue ?: "Unknown",
                )
                onChangeIndex(index)
            },
            modifier = Modifier.fillMaxWidth(),
        )
        HorizontalPager(
            state = pagerState,
        ) { tabIndex ->
            Box(
                modifier = Modifier.fillMaxSize(),
            ) {
                when (tabs[tabIndex]) {
                    is SearchDetailTab.All -> {
                        AllTab(
                            shows = shows.take(3),
                            profiles = profiles.take(3),
                            places = places.take(3),
                            onClickShow = { index, showId ->
                                AppTracker.click(
                                    screen = Screen.SearchDetail,
                                    objectRole = Role.Item,
                                    objectValue = showId,
                                    properties = mapOf(
                                        "tab" to "All",
                                        "item_category" to "Show",
                                        "item_rank" to index + 1,
                                    ),
                                )
                                onClickShow(showId)
                            },
                            onClickProfile = { index, userCode ->
                                AppTracker.click(
                                    screen = Screen.SearchDetail,
                                    objectRole = Role.Item,
                                    objectValue = userCode,
                                    properties = mapOf(
                                        "tab" to "All",
                                        "item_category" to "Artist",
                                        "item_rank" to index + 1,
                                    ),
                                )
                                onClickProfile(userCode)
                            },
                            onClickPlace = { index, placeId ->
                                AppTracker.click(
                                    screen = Screen.SearchDetail,
                                    objectRole = Role.Item,
                                    objectValue = placeId,
                                    properties = mapOf(
                                        "tab" to "All",
                                        "item_category" to "Place",
                                        "item_rank" to index + 1,
                                    ),
                                )
                                onClickPlace(placeId)
                            },
                            onClickAllShows = if (shows.size > 3) {
                                {
                                    AppTracker.click(
                                        screen = Screen.SearchDetail,
                                        objectRole = Role.Button,
                                        objectValue = "ViewAll",
                                        properties = mapOf(
                                            "tab" to "Show",
                                        ),
                                    )
                                    onChangeIndex(1)
                                }
                            } else {
                                null
                            },
                            onClickAllArtists = if (profiles.size > 3) {
                                {
                                    AppTracker.click(
                                        screen = Screen.SearchDetail,
                                        objectRole = Role.Button,
                                        objectValue = "ViewAll",
                                        properties = mapOf(
                                            "tab" to "Artist",
                                        ),
                                    )
                                    onChangeIndex(2)
                                }
                            } else {
                                null
                            },
                            onClickAllPlaces = if (places.size > 3) {
                                {
                                    AppTracker.click(
                                        screen = Screen.SearchDetail,
                                        objectRole = Role.Button,
                                        objectValue = "ViewAll",
                                        properties = mapOf(
                                            "tab" to "Place",
                                        ),
                                    )
                                    onChangeIndex(3)
                                }
                            } else {
                                null
                            },
                        )
                    }

                    is SearchDetailTab.Show -> if (shows.isEmpty()) {
                        EmptyContents(
                            keyword = keyword,
                            content = stringResource(R.string.search_no_show_result),
                        )
                    } else {
                        ShowsTab(
                            shows = shows,
                            isLoading = showsLoading,
                            onClickShow = { index, showId ->
                                AppTracker.click(
                                    screen = Screen.SearchDetail,
                                    objectRole = Role.Item,
                                    objectValue = showId,
                                    properties = mapOf(
                                        "tab" to "Show",
                                        "item_category" to "Show",
                                        "item_rank" to index + 1,
                                    ),
                                )
                                onClickShow(showId)
                            },
                            onBottomReached = onShowsPageReached,
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
                            isLoading = profilesLoading,
                            onClickProfile = { index, userCode ->
                                AppTracker.click(
                                    screen = Screen.SearchDetail,
                                    objectRole = Role.Item,
                                    objectValue = userCode,
                                    properties = mapOf(
                                        "tab" to "Artist",
                                        "item_category" to "Artist",
                                        "item_rank" to index + 1,
                                    ),
                                )
                                onClickProfile(userCode)
                            },
                            onBottomReached = onProfilesPageReached,
                        )
                    }

                    is SearchDetailTab.Place -> if (places.isEmpty()) {
                        EmptyContents(
                            keyword = keyword,
                            content = stringResource(R.string.search_no_place_result),
                        )
                    } else {
                        PlacesTab(
                            places = places,
                            isLoading = placesLoading,
                            onClickPlace = { index, placeId ->
                                AppTracker.click(
                                    screen = Screen.SearchDetail,
                                    objectRole = Role.Item,
                                    objectValue = placeId,
                                    properties = mapOf(
                                        "tab" to "Place",
                                        "item_category" to "Place",
                                        "item_rank" to index + 1,
                                    ),
                                )
                                onClickPlace(placeId)
                            },
                            onBottomReached = onPlacesPageReached,
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
    indicatorHeight: Dp = 2.dp,
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
                                    val y = size.height + verticalPadding.roundToPx() - indicatorHeight.roundToPx()
                                    drawRect(
                                        color = indicatorColor,
                                        topLeft = Offset(
                                            x = 0f,
                                            y = y,
                                        ),
                                        size = Size(width = size.width, height = indicatorHeight.toPx()),
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
private fun AllTab(
    shows: List<Show>,
    profiles: List<User.Others>,
    places: List<Place>,
    onClickShow: (Int, String) -> Unit,
    onClickProfile: (Int, UserCode) -> Unit,
    onClickPlace: (Int, String) -> Unit,
    onClickAllShows: (() -> Unit)?,
    onClickAllArtists: (() -> Unit)?,
    onClickAllPlaces: (() -> Unit)?,
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState(),
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
    ) {
        if (shows.isNotEmpty()) {
            AllTabSection(
                title = stringResource(R.string.search_tab_show_section),
                onClickAll = onClickAllShows,
            ) {
                shows.forEachIndexed { index, show ->
                    if (index > 0) Spacer(Modifier.height(20.dp))

                    ShowItem(
                        show = show,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = marginHorizontal),
                        showNameStyle = MaterialTheme.typography.titleLarge,
                        showDateStyle = MaterialTheme.typography.bodySmall.copy(color = Grey50),
                        backgroundColor = MaterialTheme.colorScheme.background,
                        contentPadding = PaddingValues(0.dp),
                        onClick = { onClickShow(index, show.id) },
                    )
                }
            }
        }

        if (shows.isNotEmpty() && profiles.isNotEmpty()) {
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = marginHorizontal),
                thickness = 1.dp,
                color = Grey85
            )
        }

        if (profiles.isNotEmpty()) {
            AllTabSection(
                title = stringResource(R.string.search_tab_artist_section),
                onClickAll = onClickAllArtists,
            ) {
                profiles.forEachIndexed { index, profile ->
                    if (index > 0) Spacer(Modifier.height(16.dp))

                    ProfileItem(
                        profile = profile,
                        onClick = { onClickProfile(index, profile.userCode) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = marginHorizontal),
                    )
                }
            }
        }

        if ((shows.isNotEmpty() || profiles.isNotEmpty()) && places.isNotEmpty()) {
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = marginHorizontal),
                thickness = 1.dp,
                color = Grey85
            )
        }

        if (places.isNotEmpty()) {
            AllTabSection(
                title = stringResource(R.string.search_tab_place_section),
                onClickAll = onClickAllPlaces,
            ) {
                places.forEachIndexed { index, place ->
                    if (index > 0) Spacer(Modifier.height(16.dp))

                    PlaceItem(
                        place = place,
                        onClick = { placeId -> onClickPlace(index, placeId) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = marginHorizontal),
                    )
                }
            }
        }
    }
}

@Composable
private fun AllTabSection(
    title: String,
    onClickAll: (() -> Unit)?,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
    ) {
        AllTabSectionTitle(
            title = title,
            onClickAll = onClickAll,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        content()
    }
}

@Composable
private fun AllTabSectionTitle(
    title: String,
    onClickAll: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = Grey05,
        )
        onClickAll?.let {
            Text(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .clickable(onClick = onClickAll)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                text = stringResource(R.string.show_all),
                style = MaterialTheme.typography.bodySmall,
                color = Grey50,
            )
        }
    }
}

@Composable
private fun ShowsTab(
    shows: List<Show>,
    isLoading: Boolean,
    onClickShow: (index: Int, id: String) -> Unit,
    onBottomReached: () -> Unit,
    modifier: Modifier = Modifier,
) {
    InfiniteScrollLazyColumn(
        isLoading = isLoading,
        onBottomReached = onBottomReached,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        itemsIndexed(shows, key = { _, show -> show.id }) { index, show ->
            ShowItem(
                show = show,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = marginHorizontal),
                showNameStyle = MaterialTheme.typography.titleLarge,
                backgroundColor = MaterialTheme.colorScheme.background,
                contentPadding = PaddingValues(0.dp),
                onClick = { onClickShow(index, show.id) },
            )
        }
    }
}

@Composable
private fun ArtistTab(
    profiles: List<User.Others>,
    isLoading: Boolean,
    onClickProfile: (Int, UserCode) -> Unit,
    onBottomReached: () -> Unit,
    modifier: Modifier = Modifier,
) {
    InfiniteScrollLazyColumn(
        isLoading = isLoading,
        onBottomReached = onBottomReached,
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        itemsIndexed(profiles, key = { _, user -> user.userCode }) { index, profile ->
            ProfileItem(
                profile = profile,
                onClick = { userCode -> onClickProfile(index, userCode) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = marginHorizontal),
            )
        }
    }
}

@Composable
private fun PlacesTab(
    places: List<Place>,
    isLoading: Boolean,
    onClickPlace: (Int, String) -> Unit,
    onBottomReached: () -> Unit,
    modifier: Modifier = Modifier,
) {
    InfiniteScrollLazyColumn(
        isLoading = isLoading,
        onBottomReached = onBottomReached,
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        itemsIndexed(places, key = { _, place -> place.id }) { index, place ->
            PlaceItem(
                place = place,
                onClick = { placeId -> onClickPlace(index, placeId) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = marginHorizontal),
            )
        }
    }
}

private sealed interface SearchDetailTab {
    val trackerValue: String

    data object All : SearchDetailTab {
        override val trackerValue: String = "All"
    }

    data class Show(val count: Long) : SearchDetailTab {
        override val trackerValue: String = "Show"
    }

    data class Artist(val count: Long) : SearchDetailTab {
        override val trackerValue: String = "Artist"
    }

    data class Place(val count: Long) : SearchDetailTab {
        override val trackerValue: String = "Place"
    }
}

@Composable
private fun SearchDetailTab.toLabel(): String = when (this) {
    is SearchDetailTab.All -> stringResource(R.string.search_tab_all)
    is SearchDetailTab.Show -> stringResource(R.string.search_tab_show, count)
    is SearchDetailTab.Artist -> stringResource(R.string.search_tab_artist, count)
    is SearchDetailTab.Place -> stringResource(R.string.search_tab_place, count)
}
