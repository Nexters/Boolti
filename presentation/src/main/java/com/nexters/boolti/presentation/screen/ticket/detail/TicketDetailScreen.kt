package com.nexters.boolti.presentation.screen.ticket.detail

import android.os.Build
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import coil.compose.AsyncImage
import com.nexters.boolti.domain.model.TicketGroup
import com.nexters.boolti.domain.model.TicketState
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BTDialog
import com.nexters.boolti.presentation.component.BtBackAppBar
import com.nexters.boolti.presentation.component.DottedDivider
import com.nexters.boolti.presentation.component.InstagramIndicator
import com.nexters.boolti.presentation.component.ShowInquiry
import com.nexters.boolti.presentation.extension.toDp
import com.nexters.boolti.presentation.extension.toPx
import com.nexters.boolti.presentation.screen.LocalSnackbarController
import com.nexters.boolti.presentation.screen.MainDestination
import com.nexters.boolti.presentation.screen.qr.QrCoverView
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.Grey10
import com.nexters.boolti.presentation.theme.Grey15
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.Grey60
import com.nexters.boolti.presentation.theme.Grey70
import com.nexters.boolti.presentation.theme.Grey80
import com.nexters.boolti.presentation.theme.Grey95
import com.nexters.boolti.presentation.theme.marginHorizontal
import com.nexters.boolti.presentation.util.TicketShape
import com.nexters.boolti.presentation.util.UrlParser
import com.nexters.boolti.presentation.util.asyncImageBlurModel
import com.nexters.boolti.presentation.util.rememberQrBitmapPainter

fun NavGraphBuilder.TicketDetailScreen(
    navigateTo: (String) -> Unit,
    popBackStack: () -> Unit,
    getSharedViewModel: @Composable (NavBackStackEntry) -> TicketDetailViewModel,
    modifier: Modifier = Modifier,
) {
    composable(
        route = "detail",
    ) { entry ->
        TicketDetailScreen(
            modifier = modifier,
            onBackClicked = popBackStack,
            onClickQr = {
                navigateTo(MainDestination.Qr.route)
            },
            navigateToShowDetail = { navigateTo("${MainDestination.ShowDetail.route}/$it") },
            viewModel = getSharedViewModel(entry),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun TicketDetailScreen(
    modifier: Modifier = Modifier,
    viewModel: TicketDetailViewModel = hiltViewModel(),
    onBackClicked: () -> Unit,
    onClickQr: () -> Unit,
    navigateToShowDetail: (showId: String) -> Unit,
) {
    val scrollState = rememberScrollState()
    var showEnterCodeDialog by remember { mutableStateOf(false) }
    var showTicketNotFoundDialog by remember { mutableStateOf(false) }
    var showRefundGiftTicket by rememberSaveable { mutableStateOf(false) }

    val snackbarHostController = LocalSnackbarController.current
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    var contentWidth by remember { mutableFloatStateOf(0f) }
    var ticketSectionHeight by remember { mutableFloatStateOf(0f) }
    var ticketSectionHeightUntilTicketInfo by remember { mutableFloatStateOf(0f) }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val managerCodeState by viewModel.managerCodeState.collectAsStateWithLifecycle()
    val ticketGroup = uiState.ticketGroup
    val pagerState = rememberPagerState { ticketGroup.tickets.size }
    val currentTicket = remember(ticketGroup, pagerState.currentPage) {
        ticketGroup.tickets.getOrElse(pagerState.currentPage) { TicketGroup.Ticket() }
    }

    val pullToRefreshState = rememberPullToRefreshState()

    val entranceSuccessMsg = stringResource(R.string.message_ticket_validated)
    val failedToRefundMsg = stringResource(R.string.refund_failed_to_registered_gift)
    val networkErrorMsg = stringResource(R.string.error_network)
    val giftRefundedString = stringResource(R.string.refund_complete_ticket_refund)
    LaunchedEffect(viewModel.event) {
        viewModel.event.collect {
            when (it) {
                TicketDetailEvent.ManagerCodeValid -> snackbarHostController.showMessage(
                    entranceSuccessMsg
                )

                TicketDetailEvent.OnRefresh -> showEnterCodeDialog = false
                TicketDetailEvent.NotFound -> showTicketNotFoundDialog = true
                TicketDetailEvent.FailedToRefund -> snackbarHostController.showMessage(
                    failedToRefundMsg
                )

                TicketDetailEvent.NetworkError -> snackbarHostController.showMessage(networkErrorMsg)
                TicketDetailEvent.GiftRefunded -> {
                    snackbarHostController.showMessage(giftRefundedString)
                }
            }
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .collect(viewModel::syncCurrentPage)
    }

    Scaffold(
        topBar = {
            BtBackAppBar(
                title = stringResource(R.string.ticket_detail_title),
                onClickBack = onBackClicked,
            )
        },
    ) { innerPadding ->
        if (pullToRefreshState.isRefreshing) {
            viewModel.refresh().invokeOnCompletion {
                pullToRefreshState.endRefresh()
            }
        }

        Box(
            modifier = Modifier
                .nestedScroll(pullToRefreshState.nestedScrollConnection)
                .padding(top = 16.dp)
        ) {
            Column(
                modifier = modifier
                    .padding(innerPadding)
                    .padding(horizontal = 29.dp)
                    .verticalScroll(scrollState),
            ) {
                val ticketShape = TicketShape(
                    width = contentWidth,
                    height = ticketSectionHeight,
                    circleRadius = 10.dp.toPx(),
                    cornerRadius = 8.dp.toPx(),
                    bottomAreaHeight = ticketSectionHeightUntilTicketInfo,
                )
                Column(
                    modifier = Modifier
                        .onGloballyPositioned { coordinates ->
                            contentWidth = coordinates.size.width.toFloat()
                            ticketSectionHeight = coordinates.size.height.toFloat()
                        }
                        .clip(ticketShape)
                        .border(
                            width = 1.dp,
                            brush = Brush.verticalGradient(
                                listOf(
                                    White.copy(.5f),
                                    White.copy(.2f)
                                )
                            ),
                            shape = ticketShape,
                        ),
                ) {
                    Box {
                        // 배경 블러된 이미지
                        Box(contentAlignment = Alignment.BottomCenter) {
                            AsyncImage(
                                model = asyncImageBlurModel(
                                    context,
                                    ticketGroup.poster,
                                    radius = 24
                                ),
                                modifier = Modifier
                                    .width(contentWidth.toDp())
                                    .aspectRatio(317 / 570f)
                                    .alpha(.8f),
                                contentScale = ContentScale.Crop,
                                contentDescription = null,
                            )
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(317 / 125f)
                                    .background(
                                        brush = Brush.verticalGradient(
                                            listOf(
                                                Black.copy(alpha = 0f),
                                                Black
                                            )
                                        ),
                                    )
                            )
                        }
                        Column(
                            modifier = Modifier
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xCCC5CACD),
                                            Grey95.copy(alpha = .8f)
                                        ),
                                    ),
                                    shape = ticketShape,
                                )
                        ) {
                            Title(showName = ticketGroup.showName)

                            QrCodes(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 20.dp)
                                    .padding(horizontal = marginHorizontal),
                                ticketGroup = ticketGroup,
                                pagerState = pagerState,
                                onClickQr = onClickQr,
                            )

                            DottedDivider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 28.dp)
                                    .padding(horizontal = marginHorizontal),
                                color = White.copy(alpha = 0.3f),
                                thickness = 2.dp
                            )

                            Column(
                                modifier = Modifier
                                    .onGloballyPositioned { coordinates ->
                                        ticketSectionHeightUntilTicketInfo =
                                            coordinates.size.height.toFloat()
                                    }
                            ) {
                                Notice(notice = ticketGroup.ticketNotice)

                                val copySuccessMessage =
                                    stringResource(id = R.string.ticketing_address_copied_message)
                                Inquiry(
                                    hostName = ticketGroup.hostName,
                                    hostPhoneNumber = ticketGroup.hostPhoneNumber,
                                    onClickCopyPlace = {
                                        clipboardManager.setText(AnnotatedString(ticketGroup.streetAddress))
                                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                                            snackbarHostController.showMessage(copySuccessMessage)
                                        }
                                    },
                                    onClickNavigateToShowDetail = {
                                        navigateToShowDetail(ticketGroup.showId)
                                    }
                                )
                            }
                        }

                        // 티켓 좌상단 꼭지점 그라데이션
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .size(105.dp)
                                .alpha(.45f)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(White, White.copy(alpha = 0f)),
                                        end = Offset(50f, 50f),
                                    )
                                ),
                        )
                    }
                }

                Spacer(modifier = Modifier.size(20.dp))
                RefundPolicySection(uiState.refundPolicy)

                if (
                    currentTicket.ticketState == TicketState.Ready &&
                    uiState.isShowDate
                ) {
                    Text(
                        modifier = Modifier
                            .padding(top = 20.dp, bottom = 60.dp)
                            .align(Alignment.CenterHorizontally)
                            .clickable { showEnterCodeDialog = true },
                        text = stringResource(R.string.input_enter_code_button),
                        style = MaterialTheme.typography.bodySmall,
                        color = Grey50,
                        textDecoration = TextDecoration.Underline,
                    )
                }

                if (uiState.isRefundableGift) {
                    Text(
                        modifier = Modifier
                            .padding(top = 20.dp, bottom = 60.dp)
                            .align(Alignment.CenterHorizontally)
                            .clickable { showRefundGiftTicket = true },
                        text = stringResource(R.string.cancel_registered_gift_button),
                        style = MaterialTheme.typography.bodySmall,
                        color = Grey50,
                        textDecoration = TextDecoration.Underline,
                    )
                }
            }
            PullToRefreshContainer(
                modifier = Modifier.align(Alignment.TopCenter),
                state = pullToRefreshState,
            )
        }
    }

    if (showEnterCodeDialog) {
        ManagerCodeDialog(
            managerCode = managerCodeState.code,
            onManagerCodeChanged = viewModel::setManagerCode,
            error = managerCodeState.error,
            onDismiss = { showEnterCodeDialog = false },
            onClickConfirm = { viewModel.requestEntrance(it) }
        )
    }

    if (showTicketNotFoundDialog) {
        BTDialog(
            enableDismiss = false,
            showCloseButton = false,
            onClickPositiveButton = {
                showTicketNotFoundDialog = false
                onBackClicked()
            },
        ) {
            Text(
                text = stringResource(R.string.error_ticket_not_found),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }

    if (showRefundGiftTicket) {
        BTDialog(
            enableDismiss = false,
            showCloseButton = false,
            onClickPositiveButton = {
                showRefundGiftTicket = false
                viewModel.refundGiftTicket()
            },
        ) {
            Text(
                text = stringResource(R.string.refund_registered_ticket_dialog),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun Title(showName: String = "") {
    Row(
        modifier = Modifier
            .background(White.copy(alpha = 0.3f))
            .alpha(0.65f)
            .padding(horizontal = 20.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = showName,
            style = MaterialTheme.typography.titleSmall,
            color = Grey80,
        )
        Image(
            modifier = Modifier
                .padding(end = 4.dp)
                .size(22.dp),
            painter = painterResource(R.drawable.ic_logo),
            colorFilter = ColorFilter.tint(Grey80),
            contentDescription = null,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun QrCodes(
    modifier: Modifier = Modifier,
    ticketGroup: TicketGroup,
    pagerState: PagerState,
    onClickQr: () -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        HorizontalPager(
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(0.75f)
                .clip(RoundedCornerShape(8.dp))
                .background(White),
            state = pagerState,
            pageSpacing = 8.dp,
        ) { i ->
            val ticket = ticketGroup.tickets[i]
            QrCode(
                modifier = Modifier.clickable(onClick = onClickQr),
                ticketName = ticketGroup.ticketName,
                qrCode = ticket.entryCode,
                csTicketId = ticket.csTicketId,
                ticketState = ticket.ticketState,
            )
        }
        if (ticketGroup.tickets.size > 1) {
            InstagramIndicator(
                modifier = Modifier.padding(top = 16.dp),
                pagerState = pagerState,
            )
        }
    }
}

@Composable
private fun QrCode(
    ticketName: String,
    qrCode: String,
    csTicketId: String,
    ticketState: TicketState,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .background(Grey10)
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                text = ticketName,
                color = Grey70,
                style = MaterialTheme.typography.titleMedium,
            )
            Box(
                modifier = Modifier
                    .height(IntrinsicSize.Max)
                    .width(IntrinsicSize.Max),
            ) {
                Image(
                    modifier = Modifier.padding(top = 16.dp),
                    painter = rememberQrBitmapPainter(qrCode, size = 178.dp),
                    contentDescription = stringResource(R.string.description_qr),
                )
                when (ticketState) {
                    TicketState.Used -> QrCoverView(
                        MaterialTheme.colorScheme.primary,
                        stringResource(R.string.ticket_used_state),
                    )

                    TicketState.Finished -> QrCoverView(
                        Grey60,
                        stringResource(R.string.ticket_show_finished_state),
                    )

                    TicketState.Ready -> Unit
                }
            }
            Text(
                modifier = Modifier
                    .padding(top = 16.dp),
                text = csTicketId,
                style = MaterialTheme.typography.bodySmall,
                color = Grey70,
            )
        }
    }
}

@Composable
private fun Notice(notice: String) {
    Column(
        modifier = Modifier
            .padding(horizontal = marginHorizontal)
            .padding(top = 36.dp, bottom = 20.dp)
            .fillMaxWidth(),
    ) {
        val uriHandler = LocalUriHandler.current
        val urlParser = remember(notice) { UrlParser(notice) }

        Text(
            text = stringResource(R.string.ticket_notice_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        ClickableText(
            modifier = Modifier.padding(top = 12.dp),
            text = urlParser.annotatedString,
            style = MaterialTheme.typography.bodySmall.copy(color = Grey30),
        ) { offset ->
            val urlOffset = urlParser.urlOffsets.find { (start, end) -> offset in start..<end }
            if (urlOffset == null) return@ClickableText
            val (start, end) = urlOffset
            val url = notice.substring(start, end)

            uriHandler.openUri(url)
        }
    }
}

@Composable
private fun Inquiry(
    hostName: String,
    hostPhoneNumber: String,
    onClickCopyPlace: () -> Unit,
    onClickNavigateToShowDetail: () -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(horizontal = marginHorizontal)
            .padding(top = 16.dp, bottom = 20.dp)
            .fillMaxWidth(),
    ) {
        Text(
            text = stringResource(R.string.ticket_show_inquiry_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        ShowInquiry(
            modifier = Modifier.padding(top = 12.dp, bottom = 20.dp),
            hostName = hostName,
            hostNumber = hostPhoneNumber
        )
        Row(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            TextButton(
                modifier = Modifier.weight(1f),
                onClick = onClickCopyPlace,
                colors = ButtonDefaults.textButtonColors(
                    containerColor = Grey70,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                contentPadding = PaddingValues(vertical = 13.dp),
                shape = RoundedCornerShape(4.dp),
            ) {
                Text(
                    text = stringResource(R.string.copy_show_place_button),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            TextButton(
                modifier = Modifier.weight(1f),
                onClick = onClickNavigateToShowDetail,
                colors = ButtonDefaults.textButtonColors(
                    containerColor = Grey15,
                    contentColor = MaterialTheme.colorScheme.surface,
                ),
                contentPadding = PaddingValues(vertical = 13.dp),
                shape = RoundedCornerShape(4.dp),
            ) {
                Text(
                    text = stringResource(R.string.navigate_to_show_button),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
private fun RefundPolicySection(refundPolicy: List<String>) {
    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 0F else 180F,
        animationSpec = tween(),
        label = "expandIconRotation"
    )
    Section(
        title = stringResource(R.string.refund_policy_label),
        titleRowOption = {
            Icon(
                modifier = Modifier
                    .clip(CircleShape)
                    .rotate(rotation)
                    .clickable(role = Role.Image) { expanded = !expanded },
                painter = painterResource(R.drawable.ic_expand_24),
                tint = Grey50,
                contentDescription = null,
            )
        },
        contentVisible = expanded,
    ) {
        Column(
            Modifier
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMedium,
                    )
                ),
            verticalArrangement = Arrangement.Center,
        ) {
            if (expanded) {
                refundPolicy.forEachIndexed { index, policy ->
                    if (index > 0) Spacer(modifier = Modifier.size(6.dp))
                    Row {
                        Text(
                            text = stringResource(R.string.bullet),
                            style = MaterialTheme.typography.bodySmall,
                            color = Grey50,
                        )
                        Text(
                            text = policy,
                            style = MaterialTheme.typography.bodySmall,
                            color = Grey50,
                            lineHeight = 22.sp,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Section(
    modifier: Modifier = Modifier,
    title: String,
    titleRowOption: (@Composable () -> Unit)? = null,
    contentVisible: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier
            .padding(bottom = 12.dp)
            .fillMaxWidth()
            .border(1.dp, color = White.copy(alpha = .15f), shape = RoundedCornerShape(8.dp))
            .padding(start = 20.dp, end = 20.dp, bottom = if (contentVisible) 20.dp else 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SectionTitle(title)
            titleRowOption?.let { it() }
        }
        content()
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(title, style = MaterialTheme.typography.titleLarge)
}

@Preview
@Composable
fun TicketDetailPreview() {
    BooltiTheme {
        Surface {
            TicketDetailScreen(
                modifier = Modifier,
                onBackClicked = {},
                onClickQr = {},
                navigateToShowDetail = {})
        }
    }
}
