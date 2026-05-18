package com.nexters.boolti.presentation.screen.place

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.nexters.boolti.domain.model.Place
import com.nexters.boolti.domain.model.PlaceContact
import com.nexters.boolti.presentation.BuildConfig
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BtAppBar
import com.nexters.boolti.presentation.component.BtAppBarDefaults
import com.nexters.boolti.presentation.component.BtCircularProgressIndicator
import com.nexters.boolti.presentation.component.BtWebView
import com.nexters.boolti.presentation.screen.LocalSnackbarController
import com.nexters.boolti.presentation.screen.showdetail.preUriLoading
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.Grey10
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.Grey70
import com.nexters.boolti.presentation.theme.Grey85
import com.nexters.boolti.presentation.theme.Grey90
import com.nexters.boolti.presentation.theme.marginHorizontal
import com.nexters.boolti.presentation.theme.point3

@Composable
fun PlaceScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlaceViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            BtCircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            PlaceContent(
                modifier = Modifier.fillMaxSize(),
                place = uiState.place,
                placeId = viewModel.placeId,
                selectedTab = uiState.selectedTab,
                onSelectTab = viewModel::selectTab,
            )
        }

        BtAppBar(
            colors = BtAppBarDefaults.appBarColors(containerColor = Color.Transparent),
            navigateButtons = {
                BtAppBarDefaults.AppBarIconButton(
                    iconRes = R.drawable.ic_arrow_back,
                    description = stringResource(R.string.description_navigate_back),
                    onClick = onBack,
                )
            },
            actionButtons = {
                BtAppBarDefaults.AppBarIconButton(
                    iconRes = R.drawable.ic_share,
                    description = stringResource(R.string.ticketing_share),
                    onClick = { TODO("공유하기") },
                )
            },
        )
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun PlaceContent(
    placeId: String,
    selectedTab: Int,
    onSelectTab: (Int) -> Unit,
    modifier: Modifier = Modifier,
    place: Place,
) {
    LazyColumn(modifier = modifier) {
        item {
            Column(
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
                    .background(color = Grey90)
            ) {
                Box(
                    contentAlignment = Alignment.BottomStart
                ) {
                    AsyncImage(
                        model = place.imageUrl,
                        contentDescription = place.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .background(MaterialTheme.colorScheme.surface),
                    )

                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Grey90.copy(alpha = 0.2f),
                                        Grey90,
                                    )
                                )
                            )
                    )

                    Text(
                        modifier = Modifier
                            .padding(horizontal = marginHorizontal)
                            .padding(top = 20.dp, bottom = 4.dp),
                        text = place.name,
                        style = point3,
                        color = Grey10,
                    )
                }

                PlaceInfoSection(place = place)

                place.contact?.let { contact ->
                    if (contact.websiteUrl != null || contact.email != null || contact.phoneNumber != null) {
                        PlaceContactSection(
                            url = contact.websiteUrl,
                            phoneNumber = contact.phoneNumber,
                            email = contact.email,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
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

        item {
            Spacer(
                Modifier
                    .navigationBarsPadding()
                    .height(16.dp)
            )
        }
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
            .padding(top = 16.dp, bottom = 24.dp)
            .padding(horizontal = marginHorizontal),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        place.rentalFee?.let { fee ->
            PlaceInfoRow(
                label = stringResource(R.string.place_rental_fee),
                value = fee,
            )
        }
        place.capacity?.let { cap ->
            PlaceInfoRow(
                label = stringResource(R.string.place_capacity),
                value = stringResource(R.string.place_capacity_format, cap),
            )
        }
        place.streetAddress?.let { address ->
            PlaceInfoRow(
                label = stringResource(R.string.place_location),
                value = address,
            )
        }
        place.subwayStation?.let { subway ->
            PlaceInfoRow(
                label = stringResource(R.string.place_subway),
                value = subway,
            )
        }
    }
}

@Composable
private fun PlaceContactSection(
    url: String?,
    phoneNumber: String?,
    email: String?,
) {
    val uriHandler = LocalUriHandler.current
    val snackbarController = LocalSnackbarController.current

    Row(
        modifier = Modifier
            .padding(horizontal = marginHorizontal)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        val noWebsiteMessage = stringResource(R.string.place_no_website)
        PlaceContactButton(
            modifier = Modifier.weight(1f),
            icon = R.drawable.ic_website,
            label = stringResource(R.string.place_website),
            enabled = url != null,
            onClick = {
                if (url != null) {
                    uriHandler.openUri(url)
                } else {
                    snackbarController.showMessage(noWebsiteMessage)
                }
            },
        )

        val noPhoneNumberMessage = stringResource(R.string.place_no_phone)
        PlaceContactButton(
            modifier = Modifier.weight(1f),
            icon = R.drawable.ic_phone,
            label = stringResource(R.string.place_phone),
            enabled = phoneNumber != null,
            onClick = {
                if (phoneNumber != null) {
                    uriHandler.openUri("tel:$phoneNumber")
                } else {
                    snackbarController.showMessage(noPhoneNumberMessage)
                }
            },
        )

        val noEmailMessage = stringResource(R.string.place_no_email)
        PlaceContactButton(
            modifier = Modifier.weight(1f),
            icon = R.drawable.ic_email,
            label = stringResource(R.string.place_email),
            enabled = email != null,
            onClick = {
                if (email != null) {
                    uriHandler.openUri("mailto:$email")
                } else {
                    snackbarController.showMessage(noEmailMessage)
                }
            },
        )
    }
}

@Composable
private fun PlaceContactButton(
    @DrawableRes icon: Int,
    label: String,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Grey85)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp)
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        val contentColor = if (enabled) Grey30 else Grey70
        Icon(
            modifier = Modifier.size(16.dp),
            imageVector = ImageVector.vectorResource(icon),
            contentDescription = label,
            tint = contentColor,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = contentColor,
        )
    }
}

@Composable
private fun PlaceInfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            modifier = Modifier.width(88.dp),
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Grey50,
        )
        Text(
            modifier = Modifier.weight(1f),
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = Grey30,
        )
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
            .padding(top = 20.dp)
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
        // TODO: 하드 코드 제거
        0 -> "https://dev.preview.boolti.in/show/298/info" // "https://$host/place/$placeId/home"
        else -> "https://dev.preview.boolti.in/show/296/info" // "https://$host/place/$placeId/rental"
    }
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    val webView by remember(tabIndex) {
        mutableStateOf(
            BtWebView(
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
                imageUrl = null,
                rentalFee = "50만원/일",
                capacity = 300,
                streetAddress = "서울시 강남구 테헤란로 123",
                subwayStation = "강남역 2번 출구",
                contact = PlaceContact(
                    websiteUrl = "https://boolti.in",
                    phoneNumber = "010-1234-5678",
                    email = "boolti@example.com",
                ),
            ),
        )
    }
}

@Preview
@Composable
fun PlaceContactSectionPreview() {
    BooltiTheme {
        PlaceContactSection(
            url = "https://boolti.in",
            phoneNumber = "010-1234-5678",
            email = "boolti@example.com",
        )
    }
}