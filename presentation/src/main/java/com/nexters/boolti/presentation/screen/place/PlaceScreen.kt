package com.nexters.boolti.presentation.screen.place

import android.annotation.SuppressLint
import android.content.Intent
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.nexters.boolti.domain.model.PlaceDetail
import com.nexters.boolti.domain.model.PlaceContact
import com.nexters.boolti.domain.model.SubwayLine
import com.nexters.boolti.domain.model.SubwayStation
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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
fun PlaceScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlaceViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val subDomain = if (BuildConfig.DEBUG) "dev.place" else "place"
    val shareUrl = "https://$subDomain.boolti.in/${viewModel.placeId}"

    val url by remember(uiState.selectedTab) {
        mutableStateOf(
            when (uiState.selectedTab) {
                0 -> "https://$subDomain.boolti.in/${viewModel.placeId}/home"
                else -> "https://$subDomain.boolti.in/${viewModel.placeId}/rental"
            }
        )
    }
    val uriHandler = LocalUriHandler.current

    val webView by remember(context) {
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
                setBackgroundColor(android.graphics.Color.TRANSPARENT)
            })
    }

    LaunchedEffect(webView, url) {
        webView.loadUrl(url)
    }

    Scaffold(
        modifier = modifier.navigationBarsPadding(),
    ) { innerPadding ->
        Box(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (uiState.isLoading) {
                BtCircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                PlaceContent(
                    modifier = Modifier.fillMaxSize(),
                    place = uiState.place,
                    selectedTab = uiState.selectedTab,
                    onSelectTab = viewModel::selectTab,
                    contentWebView = webView
                )
            }

            // 배경이 app bar 뒤에도 보여야 해서 topbar에 넣지 않음
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
                        onClick = {
                            // TODO: 유사한 케이스의 로그를 복사한 것임. 나중에 스펙 확인 후 추가할 것
//                        AppTracker.click(
//                            screen = Screen.ShowDetail,
//                            objectRole = Role.Button,
//                            objectValue = "Share",
//                            properties = mapOf(
//                                "share_method" to "LinkCopy"
//                            ),
//                        )

                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareUrl)
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, null)

                            context.startActivity(shareIntent)
                        },
                    )
                },
            )
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun PlaceContent(
    selectedTab: Int,
    onSelectTab: (Int) -> Unit,
    place: PlaceDetail,
    contentWebView: BtWebView,
    modifier: Modifier = Modifier,
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
                    val imageUrl = place.imageUrl
                    if (imageUrl != null) {
                        AsyncImage(
                            model = place.imageUrl,
                            contentDescription = place.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .background(MaterialTheme.colorScheme.surface),
                        )
                    } else {
                        Image(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f),
                            painter = painterResource(id = R.drawable.place_default_image),
                            contentDescription = null,
                        )
                    }

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
            var isLoading by remember { mutableStateOf(contentWebView.progress.value < 100)}
            val scope = rememberCoroutineScope()

            Box(
                modifier = Modifier
                    .heightIn(min = 200.dp)
                    .fillMaxWidth(),
            ) {
                if (isLoading) {
                    BtCircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                AndroidView(
                    modifier = Modifier.fillMaxWidth(),
                    factory = {
                        contentWebView.apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                            )
                            setOnLongClickListener { true }
                            progress.onEach {
                                isLoading = it < 100
                            }.launchIn(scope)
                            setWebChromeClient()
                        }
                    },
                )
            }
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
    place: PlaceDetail,
    modifier: Modifier = Modifier,
) {
    val hasAnyInfo = listOf(
        place.rentalFee,
        place.capacity?.toString(),
        place.streetAddress,
        place.contact,
    ).any { it != null } || place.subwayStations.isNotEmpty()

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
        if (place.subwayStations.isNotEmpty()) {
            PlaceStationsRow(
                label = stringResource(R.string.place_subway),
                stations = place.subwayStations,
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
private fun PlaceStationsRow(
    label: String,
    stations: List<SubwayStation>,
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
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            stations.forEach { station ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    station.lines.forEach { line ->
                        /**
                         * 글씨색깔은 디자인 시안과 다르지만, 모두 흰색으로 지정. (서버 응답과 디자인 불일치)
                         */
                        Box(
                            modifier = Modifier
                                .sizeIn(minWidth = 20.dp)
                                .clip(CircleShape)
                                .background(color = line.colorHex.toComposeColor()),
                        ) {
                            Text(
                                modifier = Modifier.padding(horizontal = 6.dp),
                                text = line.displayName,
                                style = MaterialTheme.typography.titleSmall.copy(lineHeight = 20.sp),
                                color = Color.White,
                            )
                        }
                    }

                    Text(
                        text = station.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Grey30,
                    )
                }
            }
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

private fun String.toComposeColor(): Color {
    val normalized = removePrefix("#")

    val argb = when (normalized.length) {
        6 -> "FF$normalized"
        8 -> normalized
        else -> return Grey50
    }

    return Color(argb.toLong(16))
}

@Preview
@Composable
private fun PlaceInfoSectionPreview() {
    BooltiTheme {
        PlaceInfoSection(
            place = PlaceDetail(
                id = "1",
                name = "예시 공연장",
                imageUrl = null,
                rentalFee = "50만원/일",
                capacity = 300,
                streetAddress = "서울시 강남구 테헤란로 123",
                subwayStations = listOf(
                    SubwayStation(
                        id = "1",
                        name = "왕십리",
                        lines = listOf(
                            SubwayLine(
                                id = "1",
                                name = "2",
                                colorHex = "#0CA34A",
                            ),
                            SubwayLine(
                                id = "2",
                                name = "경의",
                                colorHex = "#79C0A0",
                            ),
                            SubwayLine(
                                id = "3",
                                name = "분당",
                                colorHex = "#FCD205",
                            ),
                        )
                    )
                ),
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