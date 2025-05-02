package com.nexters.boolti.presentation.screen.showdetail

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.text.TextUtils
import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexters.boolti.domain.model.Cast
import com.nexters.boolti.domain.model.CastTeams
import com.nexters.boolti.domain.model.ShowDetail
import com.nexters.boolti.presentation.BuildConfig
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BtAppBar
import com.nexters.boolti.presentation.component.BtAppBarDefaults
import com.nexters.boolti.presentation.component.BtCircularProgressIndicator
import com.nexters.boolti.presentation.component.BtWebView
import com.nexters.boolti.presentation.component.InquiryBottomSheet
import com.nexters.boolti.presentation.component.UserThumbnail
import com.nexters.boolti.presentation.extension.asString
import com.nexters.boolti.presentation.extension.filterToPhoneNumber
import com.nexters.boolti.presentation.extension.showDateTimeString
import com.nexters.boolti.presentation.screen.ticketing.ChooseTicketBottomSheet
import com.nexters.boolti.presentation.screen.ticketing.TicketBottomSheetType
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.Grey05
import com.nexters.boolti.presentation.theme.Grey10
import com.nexters.boolti.presentation.theme.Grey20
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.Grey70
import com.nexters.boolti.presentation.theme.Grey80
import com.nexters.boolti.presentation.theme.Grey85
import com.nexters.boolti.presentation.theme.Grey90
import com.nexters.boolti.presentation.theme.marginHorizontal
import com.nexters.boolti.presentation.theme.point2
import com.nexters.boolti.presentation.theme.point3
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.net.URI
import java.net.URISyntaxException
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.math.ceil
import androidx.core.net.toUri
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.nexters.boolti.presentation.component.BTDialog
import com.nexters.boolti.presentation.theme.Grey15

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShowDetailScreen(
    onBack: () -> Unit,
    onClickHome: () -> Unit,
    navigateToLogin: () -> Unit,
    navigateToImages: (index: Int) -> Unit,
    onTicketSelected: (
        showId: String,
        ticketId: String,
        ticketCount: Int,
        isInviteTicket: Boolean,
    ) -> Unit,
    onGiftTicketSelected: (
        showId: String,
        ticketId: String,
        ticketCount: Int,
    ) -> Unit,
    navigateToReport: () -> Unit,
    navigateToProfile: (userCode: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ShowDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isLoggedIn by viewModel.loggedIn.collectAsStateWithLifecycle()

    BackHandler {
        viewModel.sendEvent(ShowDetailEvent.PopBackStack)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is ShowDetailEvent.NavigateToImages -> {
                    navigateToImages(event.index)
                }

                ShowDetailEvent.PopBackStack -> {
                    onBack()
                    viewModel.preventEvents()
                }
            }
        }
    }

    CompositionLocalProvider(LocalOverscrollConfiguration provides null) {
        Scaffold(
            modifier = modifier.navigationBarsPadding(),
            topBar = {
                ShowDetailAppBar(
                    showDetail = uiState.showDetail,
                    onBack = onBack,
                    onClickHome = onClickHome,
                    navigateToReport = navigateToReport,
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
            } else if (uiState.showDetail != null) {
                ShowDetailScreen(
                    modifier = Modifier.padding(innerPadding),
                    showDetail = uiState.showDetail!!,
                    castTeams = uiState.castTeams,
                    selectedTab = uiState.selectedTab,
                    navigateToLogin = navigateToLogin,
                    navigateToImages = { viewModel.sendEvent(ShowDetailEvent.NavigateToImages(it)) },
                    onTicketSelected = onTicketSelected,
                    onGiftTicketSelected = onGiftTicketSelected,
                    navigateToProfile = navigateToProfile,
                    isLoggedIn = isLoggedIn == true,
                    onSelectTab = viewModel::selectTab,
                    shouldShowNaverMapDialog = uiState.shouldShowNaverMapDialog,
                    doNotShowNaverMapDialog = viewModel::doNotShowNaverMapDialogAnymore,
                )
            }
        }
    }
}

@Composable
fun ShowDetailScreen(
    showDetail: ShowDetail,
    castTeams: List<CastTeams>,
    selectedTab: Int,
    navigateToLogin: () -> Unit,
    navigateToImages: (index: Int) -> Unit,
    onTicketSelected: (
        showId: String,
        ticketId: String,
        ticketCount: Int,
        isInviteTicket: Boolean,
    ) -> Unit,
    onGiftTicketSelected: (
        showId: String,
        ticketId: String,
        ticketCount: Int,
    ) -> Unit,
    navigateToProfile: (userCode: String) -> Unit,
    isLoggedIn: Boolean,
    onSelectTab: (index: Int) -> Unit,
    shouldShowNaverMapDialog: Boolean,
    doNotShowNaverMapDialog: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val showState by flow {
        while (true) {
            emit(showDetail.state)
            delay(200)
        }
    }.collectAsStateWithLifecycle(showDetail.state)

    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf<TicketBottomSheetType?>(null) }

    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        val showCountdownBanner =
            showDetail.salesEndDateTime.toLocalDate() == LocalDate.now()

        var buttonsHeight by remember { mutableStateOf(0.dp) }

        LazyColumn {
            item {
                val paddingTop = if (showCountdownBanner) (38 + 40).dp else 16.dp

                Poster(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(
                            shape = RoundedCornerShape(
                                bottomStart = 20.dp,
                                bottomEnd = 20.dp
                            )
                        )
                        .background(color = MaterialTheme.colorScheme.surface)
                        .padding(top = paddingTop),
                    navigateToImages = navigateToImages,
                    showDetail = showDetail,
                )
            }

            item {
                ContentTabRow(
                    modifier = Modifier.padding(top = 20.dp),
                    selectedTabIndex = selectedTab,
                    onSelectTab = onSelectTab,
                )
            }

            when (selectedTab) {
                0 -> ShowInfoTab(
                    showId = showDetail.id,
                    shouldShowNaverMapDialog = shouldShowNaverMapDialog,
                    doNotShowNaverMapDialog = doNotShowNaverMapDialog
                )

                1 -> CastTab(
                    teams = castTeams,
                    onClickMember = navigateToProfile,
                )
            }

            item { Spacer(modifier = Modifier.size(buttonsHeight)) }
        }

        val onTicketClicked: (TicketBottomSheetType) -> Unit = { type ->
            scope.launch {
                if (isLoggedIn) {
                    showBottomSheet = type
                } else {
                    navigateToLogin()
                }
            }
        }

        AnimatedVisibility(
            modifier = Modifier.align(Alignment.BottomCenter),
            visible = !showState.isClosedOrFinished,
            enter = EnterTransition.None,
            exit = fadeOut() + shrinkOut(shrinkTowards = Alignment.TopCenter),
        ) {
            ShowDetailButtons(
                showState = showState,
                onTicketingClicked = { onTicketClicked(TicketBottomSheetType.PURCHASE) },
                onGiftClicked = { onTicketClicked(TicketBottomSheetType.GIFT) },
                onHeightChanged = { buttonsHeight = it },
            )
        }

        if (showCountdownBanner) {
            CountDownBanner(
                deadlineDateTime = showDetail.salesEndDateTime,
            )
        }
    }

    showBottomSheet?.let { type ->
        ChooseTicketBottomSheet(
            ticketType = type,
            onTicketingClicked = { ticket, count ->
                Timber.tag("MANGBAAM-(TicketScreen)").d("선택된 티켓: $ticket")
                onTicketSelected(
                    showDetail.id,
                    ticket.id,
                    count,
                    ticket.isInviteTicket,
                )
                showBottomSheet = null
            },
            onGiftTicketClicked = { ticket, count ->
                onGiftTicketSelected(
                    showDetail.id,
                    ticket.id,
                    count,
                )
                showBottomSheet = null
            },
            onDismissRequest = {
                showBottomSheet = null
            }
        )
    }
}

@Composable
private fun ShowDetailAppBar(
    showDetail: ShowDetail?,
    onBack: () -> Unit,
    onClickHome: () -> Unit,
    navigateToReport: () -> Unit,
) {
    var isContextMenuVisible by rememberSaveable {
        mutableStateOf(false)
    }
    var showShareBottomSheet by rememberSaveable { mutableStateOf(false) }

    BtAppBar(
        colors = BtAppBarDefaults.appBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        navigateButtons = {
            BtAppBarDefaults.AppBarIconButton(
                iconRes = R.drawable.ic_arrow_back,
                description = stringResource(id = R.string.description_navigate_back),
                onClick = onBack,
            )
            BtAppBarDefaults.AppBarIconButton(
                iconRes = R.drawable.ic_home,
                description = stringResource(id = R.string.description_toolbar_home),
                onClick = onClickHome,
            )
        },
        actionButtons = {
            if (showDetail == null) return@BtAppBar

            BtAppBarDefaults.AppBarIconButton(
                iconRes = R.drawable.ic_share,
                description = stringResource(id = R.string.ticketing_share),
                onClick = {
                    showShareBottomSheet = true
                },
            )
            BtAppBarDefaults.AppBarIconButton(
                iconRes = R.drawable.ic_verticle_more,
                description = stringResource(id = R.string.description_more_menu),
                onClick = { isContextMenuVisible = true },
            )
        },
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.TopEnd),
    ) {
        DropdownMenu(
            modifier = Modifier.background(Grey20),
            expanded = isContextMenuVisible,
            onDismissRequest = { isContextMenuVisible = false },
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(id = R.string.report),
                        color = Color.Black,
                    )
                },
                onClick = {
                    navigateToReport()
                    isContextMenuVisible = false
                },
            )
        }
    }

    if (showShareBottomSheet && showDetail != null) {
        ShareBottomSheet(
            showDetail = showDetail,
            onDismiss = {
                showShareBottomSheet = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShareBottomSheet(showDetail: ShowDetail, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val dateString = "${showDetail.date.showDateTimeString} -"
    val addressString =
        "${showDetail.placeName} / ${showDetail.streetAddress}, ${showDetail.detailAddress}"
    val devPrefix = if (BuildConfig.DEBUG) "dev." else ""
    val previewUrl = "https://${devPrefix}preview.boolti.in/show/${showDetail.id}"
    val sharingText = stringResource(
        R.string.show_share_format,
        showDetail.name,
        dateString,
        addressString,
        previewUrl
    )

    ModalBottomSheet(
        containerColor = Grey85,
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 24.dp)
                    .size(45.dp, 4.dp)
                    .clip(CircleShape)
                    .background(Grey70),
            )
        },
    ) {
        Column(
            modifier = Modifier.padding(bottom = 28.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .clickable {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, previewUrl)
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, null)

                        context.startActivity(shareIntent)
                    },
                contentAlignment = Alignment.CenterStart,
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    text = stringResource(R.string.ticketing_share_only_url),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Grey10,
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .clickable {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, sharingText)
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, null)

                        context.startActivity(shareIntent)
                    },
                contentAlignment = Alignment.CenterStart,
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    text = stringResource(R.string.ticketing_share_with_info),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Grey10,
                )
            }
        }
    }
}

@Composable
private fun ContentTabRow(
    modifier: Modifier = Modifier,
    selectedTabIndex: Int = 0,
    onSelectTab: (index: Int) -> Unit = {},
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
            ContentTab(
                selected = selectedTabIndex == 0,
                label = stringResource(R.string.show_tab_info),
                onSelect = { onSelectTab(0) },
            )
            ContentTab(
                selected = selectedTabIndex == 1,
                label = stringResource(R.string.show_tab_cast),
                onSelect = { onSelectTab(1) },
            )
        }
    }
}

@Composable
private fun ContentTab(
    modifier: Modifier = Modifier,
    selected: Boolean,
    label: String,
    onSelect: () -> Unit,
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
@Suppress("FunctionName")
private fun LazyListScope.ShowInfoTab(
    showId: String,
    shouldShowNaverMapDialog: Boolean,
    doNotShowNaverMapDialog: () -> Unit,
) {
    item {
        var redirectedInquiryUrl: String? by remember { mutableStateOf(null) }
        val host = if (BuildConfig.DEBUG) "dev.preview.boolti.in" else "preview.boolti.in"
        val url = "https://${host}/show/${showId}/info"
        val context = LocalContext.current
        val uriHandler = LocalUriHandler.current
        var intentToNavigateTo: Intent? by remember { mutableStateOf(null) }

        // ex. tel:010-1010-1101
        val telSchemes = listOf("tel", "telprompt")
        val webView by remember {
            mutableStateOf(BtWebView(preUriLoading = { url ->
                preUriLoading(
                    url = url,
                    context = context,
                    uriHandler = uriHandler,
                    navigateWithUrl = { url -> redirectedInquiryUrl = url },
                    navigateWithIntent = { intent -> intentToNavigateTo = intent })
            }, context = context).apply {
                loadUrl(url)
                setBackgroundColor(android.graphics.Color.TRANSPARENT)
            })
        }

        Box(
            modifier = Modifier
                .heightIn(min = 96.dp)
                .fillMaxWidth()
        ) {
            BtCircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
            AndroidView(
                modifier = Modifier.fillMaxWidth(),
                factory = { context ->
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

        redirectedInquiryUrl?.let { url ->
            val contact = url.filterToPhoneNumber()
            val isPhone = URI(url).scheme in telSchemes

            InquiryBottomSheet(
                isTelephone = isPhone,
                onDismissRequest = { redirectedInquiryUrl = null },
                contact = contact
            )
        }

        if (intentToNavigateTo != null) {
            if (shouldShowNaverMapDialog) {
                BTDialog(
                    onDismiss = {
                        intentToNavigateTo = null
                    },
                    positiveButtonLabel = stringResource(R.string.show_navigate_to_nmap),
                    onClickPositiveButton = {
                        context.startActivity(intentToNavigateTo)
                        doNotShowNaverMapDialog()
                    }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = stringResource(R.string.show_naver_map_dialog_title),
                            color = Grey15,
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center,
                        )
                        Text(
                            modifier = Modifier.padding(
                                top = 4.dp
                            ),
                            text = stringResource(R.string.show_naver_map_dialog_content),
                            style = MaterialTheme.typography.bodySmall,
                            color = Grey50,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            } else {
                context.startActivity(intentToNavigateTo)
            }
            intentToNavigateTo = null
        }
    }

    // 최하단 섹션의 하단 패딩
    item { Spacer(Modifier.size(16.dp)) }
}

fun preUriLoading(
    url: String,
    context: Context,
    uriHandler: UriHandler,
    navigateWithIntent: (Intent?) -> Unit,
    navigateWithUrl: (String) -> Unit,
): Boolean {
    val scheme = URI(url).scheme

    if (scheme == "intent") {
        var intent = getIntentFromUri(url) ?: return false

        if (TextUtils.isEmpty(intent.`package`)) {
            return false
        }

        val canHandle = context.packageManager.queryIntentActivities(
            intent,
            PackageManager.MATCH_DEFAULT_ONLY
        ).isNotEmpty()
        if (canHandle) {
            navigateWithIntent(intent)
        } else {
            if (intent.`package` != "com.nhn.android.nmap") return false

            val fallbackUri = url.substringBefore("#intent").toUri()

            val lat = fallbackUri.getQueryParameter("lat")
            val lng = fallbackUri.getQueryParameter("lng")
            val name = fallbackUri.getQueryParameter("name")

            uriHandler.openUri("https://map.naver.com/?lat=${lat}&lng=${lng}&title=${name}")
        }
        return true
    }

    val telSchemes = listOf("tel", "telprompt")
    val textSchemes = listOf("sms", "smsto", "mms", "mmsto")
    if (scheme in telSchemes + textSchemes) {
        navigateWithUrl(url)

        return true
    }

    return false
}

fun getIntentFromUri(uri: String): Intent? {
    try {
        return Intent.parseUri(uri, Intent.URI_INTENT_SCHEME)
    } catch (e: URISyntaxException) {
        FirebaseCrashlytics.getInstance().recordException(e)
        return null
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Suppress("FunctionName")
fun LazyListScope.CastTab(
    teams: List<CastTeams>,
    onClickMember: (userCode: String) -> Unit,
) {
    val paddingModifier = Modifier.padding(horizontal = marginHorizontal)

    if (teams.isEmpty()) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(290.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.empty_cast_title),
                    style = point2,
                    color = Grey20,
                )
                Text(
                    modifier = Modifier.padding(top = 2.dp),
                    text = stringResource(R.string.empty_cast_desc),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Grey30,
                )
            }
        }
    } else {
        itemsIndexed(teams) { index, team ->
            if (index > 0) Divider(paddingModifier) else Spacer(modifier = Modifier.size(8.dp))
            Section(
                modifier = paddingModifier,
                title = { SectionTitle(title = team.teamName) },
                space = if (team.members.isNotEmpty()) 20.dp else 0.dp,
                paddingVertical = 24.dp,
                content = {
                    val spacedBySize = 20.dp
                    val memberHeight = 46.dp
                    val spanCount = 2
                    val rows = ceil(team.members.size / spanCount.toFloat())

                    /**
                     * 중첩 Lazy 레이아웃 처리를 위해 높이 고정 필요
                     */
                    val gridHeight = memberHeight * rows + spacedBySize * (rows - 1)
                    LazyVerticalGrid(
                        modifier = Modifier.height(gridHeight),
                        columns = GridCells.Fixed(spanCount),
                        verticalArrangement = Arrangement.spacedBy(spacedBySize),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        items(team.members) { member ->
                            Cast(
                                memberHeight,
                                member,
                                onClick = { onClickMember(member.userCode) },
                            )
                        }
                    }
                }
            )
            if (index == teams.lastIndex) Spacer(modifier = Modifier.size(8.dp))
        }
    }
}

@Composable
private fun Poster(
    showDetail: ShowDetail,
    navigateToImages: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val images by remember { derivedStateOf { showDetail.images.map { it.originImage } } }

    Column(
        modifier = modifier.padding(horizontal = 38.dp)
    ) {
        SwipeableImage(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(8.dp))
                .border(width = 1.dp, color = Grey80, shape = RoundedCornerShape(8.dp)),
            models = images,
            onImageClick = navigateToImages,
        )
        Text(
            modifier = Modifier.padding(top = 24.dp),
            text = showDetail.name,
            style = point3,
        )
        Row(
            modifier = Modifier.padding(top = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                modifier = Modifier.size(20.dp),
                imageVector = ImageVector.vectorResource(R.drawable.ic_time),
                tint = Grey50,
                contentDescription = null,
            )
            Text(
                text = showDetail.date.showDateTimeString,
                style = MaterialTheme.typography.bodyLarge.copy(color = Grey30),
            )
            Box(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = Grey50,
                        shape = CircleShape,
                    )
                    .padding(horizontal = 8.dp, vertical = 2.dp),
            ) {
                val minute = stringResource(id = R.string.ticketing_minutes)
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = "${showDetail.runningTime}${minute}",
                    style = MaterialTheme.typography.labelMedium.copy(color = Grey30),
                )
            }
        }
        Row(
            modifier = Modifier.padding(top = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier.size(20.dp),
                imageVector = ImageVector.vectorResource(R.drawable.ic_place),
                tint = Grey50,
                contentDescription = null,
            )
            Text(
                modifier = Modifier.padding(start = 6.dp),
                text = showDetail.placeName,
                style = MaterialTheme.typography.bodyLarge.copy(color = Grey30),
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun Section(
    title: @Composable () -> Unit,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    paddingVertical: Dp = 24.dp,
    space: Dp = 16.dp,
) {
    Column(modifier.padding(vertical = paddingVertical)) {
        title()
        Spacer(modifier = Modifier.height(space))
        content()
    }
}

@Composable
private fun SectionTitle(
    title: String,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier,
        text = title,
        style = MaterialTheme.typography.titleLarge,
    )
}

@Composable
private fun Cast(
    memberHeight: Dp,
    member: Cast,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .height(memberHeight)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        UserThumbnail(
            model = member.photo,
            size = 46.dp,
            contentDescription = member.nickname,
        )
        Column(
            modifier = Modifier.padding(start = 8.dp),
        ) {
            Text(
                text = member.nickname,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = member.roleName,
                style = MaterialTheme.typography.bodySmall,
                color = Grey50,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun Divider(modifier: Modifier = Modifier) {
    HorizontalDivider(modifier = modifier, color = Grey85)
}

@Composable
private fun CountDownBanner(
    deadlineDateTime: LocalDateTime,
) {
    val remainingTime by flow {
        while (true) {
            val duration = Duration.between(
                LocalDateTime.now(),
                deadlineDateTime
            )
            emit(maxOf(duration, Duration.ZERO))
            if (duration <= Duration.ZERO) break
            delay(200L)
        }
    }.collectAsStateWithLifecycle(Duration.ZERO)

    Box(
        modifier = Modifier
            .height(40.dp)
            .fillMaxWidth()
            .background(Grey05),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(id = R.string.show_ticketing_deadline_countdown) + " " + remainingTime.asString(),
            style = MaterialTheme.typography.titleMedium.copy(color = Grey90)
        )
    }
}

@Preview
@Composable
private fun ShowDetailScreenPreview() {
    BooltiTheme {
        ShowDetailScreen(
            showDetail = ShowDetail(),
            castTeams = emptyList(),
            selectedTab = 0,
            navigateToLogin = {},
            navigateToImages = {},
            onTicketSelected = { _, _, _, _ -> },
            onGiftTicketSelected = { _, _, _ -> },
            navigateToProfile = {},
            isLoggedIn = true,
            onSelectTab = {},
            shouldShowNaverMapDialog = false,
            doNotShowNaverMapDialog = {},
        )
    }
}

@Preview
@Composable
private fun CountDownBannerPreview() {
    BooltiTheme {
        CountDownBanner(
            deadlineDateTime = LocalDateTime.now()
                .plusHours(0)
                .plusMinutes(5)
                .plusSeconds(12),
        )
    }
}
