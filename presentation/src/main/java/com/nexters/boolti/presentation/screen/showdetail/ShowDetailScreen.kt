package com.nexters.boolti.presentation.screen.showdetail

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.Firebase
import com.google.firebase.dynamiclinks.androidParameters
import com.google.firebase.dynamiclinks.dynamicLinks
import com.google.firebase.dynamiclinks.iosParameters
import com.google.firebase.dynamiclinks.shortLinkAsync
import com.nexters.boolti.domain.model.ShowDetail
import com.nexters.boolti.domain.model.ShowState
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BtAppBar
import com.nexters.boolti.presentation.component.BtAppBarDefaults
import com.nexters.boolti.presentation.component.MainButton
import com.nexters.boolti.presentation.component.MainButtonDefaults
import com.nexters.boolti.presentation.component.ShowInquiry
import com.nexters.boolti.presentation.component.SmallButton
import com.nexters.boolti.presentation.extension.requireActivity
import com.nexters.boolti.presentation.screen.LocalSnackbarController
import com.nexters.boolti.presentation.screen.ticketing.ChooseTicketBottomSheet
import com.nexters.boolti.presentation.screen.ticketing.TicketBottomSheetType
import com.nexters.boolti.presentation.theme.Grey20
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.Grey80
import com.nexters.boolti.presentation.theme.Grey85
import com.nexters.boolti.presentation.theme.marginHorizontal
import com.nexters.boolti.presentation.theme.point3
import com.nexters.boolti.presentation.util.UrlParser
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.format.DateTimeFormatter

@Composable
fun ShowDetailScreen(
    onBack: () -> Unit,
    onClickHome: () -> Unit,
    onClickContent: () -> Unit,
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
    modifier: Modifier = Modifier,
    viewModel: ShowDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isLoggedIn by viewModel.loggedIn.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf<TicketBottomSheetType?>(null) }

    val window = LocalContext.current.requireActivity().window
    window.statusBarColor = MaterialTheme.colorScheme.surface.toArgb()

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

    BackHandler {
        viewModel.sendEvent(ShowDetailEvent.PopBackStack)
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            ShowDetailAppBar(
                showId = uiState.showDetail.id,
                onBack = { viewModel.sendEvent(ShowDetailEvent.PopBackStack) },
                onClickHome = onClickHome,
                navigateToReport = navigateToReport,
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier.verticalScroll(scrollState),
            ) {
                Poster(
                    modifier = modifier.fillMaxWidth(),
                    navigateToImages = { viewModel.sendEvent(ShowDetailEvent.NavigateToImages(it)) },
                    title = uiState.showDetail.name,
                    images = uiState.showDetail.images.map { it.originImage }
                )
                ContentScaffold(
                    modifier = Modifier
                        .padding(horizontal = marginHorizontal)
                        .padding(bottom = 114.dp),
                    showDetail = uiState.showDetail,
                    host = stringResource(
                        id = R.string.ticketing_host_format,
                        uiState.showDetail.hostName,
                        uiState.showDetail.hostPhoneNumber,
                    ),
                    onClickContent = onClickContent,
                )
            }

            val onTicketClicked: (TicketBottomSheetType) -> Unit = { type ->
                scope.launch {
                    if (isLoggedIn == true) {
                        showBottomSheet = type
                    } else {
                        navigateToLogin()
                    }
                }
            }

            ShowDetailButtons(
                showState = uiState.showDetail.state,
                onTicketingClicked = { onTicketClicked(TicketBottomSheetType.PURCHASE) },
                onGiftClicked = { onTicketClicked(TicketBottomSheetType.GIFT) }
            )
        }

        showBottomSheet?.let { type ->
            ChooseTicketBottomSheet(
                ticketType = type,
                onTicketingClicked = { ticket, count ->
                    Timber.tag("MANGBAAM-(TicketScreen)").d("선택된 티켓: $ticket")
                    onTicketSelected(
                        uiState.showDetail.id,
                        ticket.id,
                        count,
                        ticket.isInviteTicket,
                    )
                    showBottomSheet = null
                },
                onGiftTicketClicked = { ticket, count ->
                    onGiftTicketSelected(
                        uiState.showDetail.id,
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
}

@Composable
private fun ShowDetailAppBar(
    showId: String,
    onBack: () -> Unit,
    onClickHome: () -> Unit,
    navigateToReport: () -> Unit,
) {
    val context = LocalContext.current
    var isContextMenuVisible by rememberSaveable {
        mutableStateOf(false)
    }
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
            BtAppBarDefaults.AppBarIconButton(
                iconRes = R.drawable.ic_share,
                description = stringResource(id = R.string.ticketing_share),
                onClick = {
                    Firebase.dynamicLinks.shortLinkAsync {
                        val uri = Uri.parse("https://preview.boolti.in/show/$showId")
                        link = uri
                        domainUriPrefix = "https://boolti.page.link"

                        androidParameters {
                            fallbackUrl = uri
                        }
                        iosParameters("com.nexters.boolti") {
                            setFallbackUrl(uri)
                        }
                    }.addOnSuccessListener {
                        it.shortLink?.let { link ->
                            println(link)

                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    link.toString()
                                )
                                type = "text/plain"
                            }

                            val shareIntent = Intent.createChooser(sendIntent, null)
                            context.startActivity(shareIntent)
                        }
                    }
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
}

@Composable
private fun ContentScaffold(
    showDetail: ShowDetail,
    host: String,
    onClickContent: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarController = LocalSnackbarController.current

    Column(
        modifier = modifier,
    ) {
        TicketReservationPeriod(
            modifier = Modifier.padding(top = 40.dp),
            startDate = showDetail.salesStartDate,
            endDate = showDetail.salesEndDate,
        )

        // 일시
        val daysOfWeek = stringArrayResource(id = R.array.days_of_week)
        val indexOfDay = showDetail.date.dayOfWeek.value - 1
        val minute = stringResource(id = R.string.ticketing_minutes)
        // ex. 2024.01.20 (토) / 18:00 (150분)
        val formatter =
            DateTimeFormatter.ofPattern("yyyy.MM.dd (${daysOfWeek[indexOfDay]}) / HH:mm (${showDetail.runningTime}${minute})")
        Section(
            title = { SectionTitle(stringResource(id = R.string.ticketing_datetime)) },
            content = { SectionContent(text = showDetail.date.format(formatter)) },
        )
        Divider(color = Grey85)

        // 장소
        Section(
            title = {
                Row(
                    modifier = Modifier.height(30.dp)
                ) {
                    SectionTitle(stringResource(id = R.string.ticketing_place))
                    Spacer(modifier = modifier.weight(1.0f))
                    val clipboardManager = LocalClipboardManager.current
                    val copiedMessage =
                        stringResource(id = R.string.ticketing_address_copied_message)
                    SmallButton(
                        iconRes = R.drawable.ic_copy,
                        label = stringResource(id = R.string.ticketing_copy_address),
                        onClick = {
                            clipboardManager.setText(AnnotatedString(showDetail.streetAddress))
                            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                                snackbarController.showMessage(copiedMessage)
                            }
                        }
                    )
                }
            },
            content = {
                Column {
                    Text(showDetail.placeName, style = MaterialTheme.typography.bodyLarge)
                    SectionContent(
                        modifier = Modifier.padding(top = 8.dp),
                        text = "${showDetail.streetAddress} / ${showDetail.detailAddress}"
                    )
                }
            },
        )
        Divider(color = Grey85)

        // 공연 내용
        Section(
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    SectionTitle(stringResource(id = R.string.ticketing_content))
                    Text(
                        modifier = Modifier.clickable(onClick = onClickContent),
                        text = stringResource(id = R.string.ticketing_all_content),
                        style = MaterialTheme.typography.bodySmall.copy(color = Grey50),
                    )
                }
            },
            content = {
                SectionContent(
                    showDetail.notice,
                    overflow = TextOverflow.Ellipsis,
                )
            },
        )
        Divider(color = Grey85)

        // 주최자
        Section(
            title = { SectionTitle(stringResource(id = R.string.ticketing_host)) },
            content = {
                ShowInquiry(
                    hostName = host.substringBefore("("),
                    hostNumber = host.substringAfter("(").substringBefore(")")
                )
            },
        )
    }
}

@Composable
private fun Poster(
    images: List<String>,
    title: String,
    navigateToImages: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
            .background(color = MaterialTheme.colorScheme.surface)
            .padding(top = 16.dp)
            .padding(horizontal = 38.dp)
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
            modifier = Modifier.padding(top = 24.dp, bottom = 30.dp),
            text = title,
            style = point3,
        )
    }
}

@Composable
private fun Section(
    title: @Composable () -> Unit, content: @Composable () -> Unit, modifier: Modifier = Modifier,
) {
    Column(modifier.padding(top = 40.dp, bottom = 32.dp)) {
        title()
        Spacer(modifier = Modifier.height(16.dp))
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
private fun SectionContent(
    text: String,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
) {
    val uriHandler = LocalUriHandler.current
    val urlParser = UrlParser(text)

    ClickableText(
        modifier = modifier.heightIn(0.dp, 246.dp),
        text = urlParser.annotatedString,
        style = MaterialTheme.typography.bodyLarge.copy(color = Grey30),
        maxLines = maxLines,
        overflow = overflow,
    ) { offset ->
        val urlOffset = urlParser.urlOffsets.find { (start, end) -> offset in start..<end }
        if (urlOffset == null) return@ClickableText
        val (start, end) = urlOffset
        val url = text.substring(start, end)

        uriHandler.openUri(url)
    }
}
