package com.nexters.boolti.presentation.screen.place

import android.annotation.SuppressLint
import android.content.Intent
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.platform.LocalUriHandler
import com.nexters.boolti.domain.model.Place
import com.nexters.boolti.presentation.BuildConfig
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BtAppBar
import com.nexters.boolti.presentation.component.BtAppBarDefaults
import com.nexters.boolti.presentation.component.BtCircularProgressIndicator
import com.nexters.boolti.presentation.component.BtWebView
import com.nexters.boolti.presentation.screen.showdetail.preUriLoading
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.Grey70
import com.nexters.boolti.presentation.theme.Grey85
import com.nexters.boolti.presentation.theme.marginHorizontal

@Composable
fun PlaceScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlaceViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = {
            BtAppBar(
                navigateButtons = {
                    BtAppBarDefaults.AppBarIconButton(
                        iconRes = R.drawable.ic_arrow_back,
                        description = stringResource(R.string.description_navigate_back),
                        onClick = onBack,
                    )
                },
            )
        },
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                BtCircularProgressIndicator()
            }
        } else {
            PlaceContent(
                modifier = Modifier.padding(innerPadding),
                place = uiState.place,
                placeId = viewModel.placeId,
                selectedTab = uiState.selectedTab,
                onSelectTab = viewModel::selectTab,
            )
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun PlaceContent(
    placeId: String,
    selectedTab: Int,
    onSelectTab: (Int) -> Unit,
    modifier: Modifier = Modifier,
    place: Place? = null,
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        place?.let {
            item {
                PlaceInfoSection(place = it)
            }
        }

        item {
            PlaceTabRow(
                selectedTabIndex = selectedTab,
                onSelectTab = onSelectTab,
            )
        }

        item {
            PlaceWebView(placeId = placeId, tabIndex = selectedTab)
        }

        item { Spacer(Modifier.size(16.dp)) }
    }
}

@Composable
private fun PlaceInfoSection(
    place: Place,
    modifier: Modifier = Modifier,
) {
    val hasAnyInfo = listOf(
        place.rentalFee,
        place.capacity?.toString(),
        place.streetAddress,
        place.subwayStation,
        place.contact,
    ).any { it != null }

    if (!hasAnyInfo) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = marginHorizontal, vertical = 20.dp),
    ) {
        place.rentalFee?.let { fee ->
            PlaceInfoRow(
                iconRes = R.drawable.ic_info_20,
                label = stringResource(R.string.place_rental_fee),
                value = fee,
            )
        }
        place.capacity?.let { cap ->
            PlaceInfoRow(
                iconRes = R.drawable.ic_person,
                label = stringResource(R.string.place_capacity),
                value = stringResource(R.string.place_capacity_format, cap),
            )
        }
        place.streetAddress?.let { address ->
            PlaceInfoRow(
                iconRes = R.drawable.ic_place,
                label = stringResource(R.string.place_location),
                value = address,
            )
        }
        place.subwayStation?.let { subway ->
            PlaceInfoRow(
                iconRes = R.drawable.ic_place,
                label = stringResource(R.string.place_subway),
                value = subway,
            )
        }
        place.contact?.let { contact ->
            PlaceInfoRow(
                iconRes = R.drawable.ic_telephone,
                label = stringResource(R.string.place_contact),
                value = contact,
            )
        }
    }
}

@Composable
private fun PlaceInfoRow(
    iconRes: Int,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            imageVector = ImageVector.vectorResource(iconRes),
            tint = Grey30,
            contentDescription = label,
        )
        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = Grey50,
            )
            Text(
                modifier = Modifier.padding(top = 2.dp),
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun PlaceTabRow(
    selectedTabIndex: Int,
    onSelectTab: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background),
    ) {
        HorizontalDivider(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(),
            thickness = 1.dp,
            color = Grey85,
        )
        TabRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = marginHorizontal),
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.Transparent,
            indicator = { tabPositions ->
                if (selectedTabIndex < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            },
            divider = {},
        ) {
            PlaceTab(
                selected = selectedTabIndex == 0,
                label = stringResource(R.string.place_tab_home),
                onSelect = { onSelectTab(0) },
            )
            PlaceTab(
                selected = selectedTabIndex == 1,
                label = stringResource(R.string.place_tab_rental),
                onSelect = { onSelectTab(1) },
            )
        }
    }
}

@Composable
private fun PlaceTab(
    selected: Boolean,
    label: String,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Tab(
        modifier = modifier,
        selected = selected,
        selectedContentColor = MaterialTheme.colorScheme.onSurface,
        unselectedContentColor = Grey70,
        text = {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
            )
        },
        onClick = onSelect,
    )
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun PlaceWebView(
    placeId: String,
    tabIndex: Int,
) {
    val host = if (BuildConfig.DEBUG) "dev.preview.boolti.in" else "preview.boolti.in"
    val url = when (tabIndex) {
        0 -> "https://$host/place/$placeId/home"
        else -> "https://$host/place/$placeId/rental"
    }
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    val webView by remember(tabIndex) {
        mutableStateOf(BtWebView(
            preUriLoading = { loadUrl ->
                preUriLoading(
                    url = loadUrl,
                    context = context,
                    uriHandler = uriHandler,
                    navigateWithIntent = { intent -> intent?.let { context.startActivity(it) } },
                    navigateWithUrl = {},
                )
            },
            context = context,
        ).apply {
            loadUrl(url)
            setBackgroundColor(android.graphics.Color.TRANSPARENT)
        })
    }

    Box(
        modifier = Modifier
            .heightIn(min = 200.dp)
            .fillMaxWidth(),
    ) {
        BtCircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = {
                webView.apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                    )
                    setOnLongClickListener { true }
                }
            },
        )
    }
}

@Preview
@Composable
private fun PlaceInfoSectionPreview() {
    BooltiTheme {
        PlaceInfoSection(
            place = Place(
                id = "1",
                name = "예시 공연장",
                rentalFee = "50만원/일",
                capacity = 300,
                streetAddress = "서울시 강남구 테헤란로 123",
                subwayStation = "강남역 2번 출구",
                contact = "010-1234-5678",
            ),
        )
    }
}

@Preview
@Composable
private fun PlaceInfoSectionEmptyPreview() {
    BooltiTheme {
        PlaceInfoSection(
            place = Place(
                id = "1",
                name = "예시 공연장",
                rentalFee = null,
                capacity = null,
                streetAddress = null,
                subwayStation = null,
                contact = null,
            ),
        )
    }
}
