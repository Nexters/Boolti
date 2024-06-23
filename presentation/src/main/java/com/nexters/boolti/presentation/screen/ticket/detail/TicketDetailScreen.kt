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
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
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
import com.nexters.boolti.presentation.component.ToastSnackbarHost
import com.nexters.boolti.presentation.extension.dayOfWeekString
import com.nexters.boolti.presentation.extension.format
import com.nexters.boolti.presentation.extension.toDp
import com.nexters.boolti.presentation.extension.toPx
import com.nexters.boolti.presentation.screen.MainDestination
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.Grey10
import com.nexters.boolti.presentation.theme.Grey20
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey40
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.Grey70
import com.nexters.boolti.presentation.theme.Grey80
import com.nexters.boolti.presentation.theme.Grey95
import com.nexters.boolti.presentation.theme.marginHorizontal
import com.nexters.boolti.presentation.theme.point2
import com.nexters.boolti.presentation.theme.point4
import com.nexters.boolti.presentation.util.TicketShape
import com.nexters.boolti.presentation.util.asyncImageBlurModel
import com.nexters.boolti.presentation.util.rememberQrBitmapPainter
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

fun NavGraphBuilder.TicketDetailScreen(
    navigateTo: (String) -> Unit,
    popBackStack: () -> Unit,
    getSharedViewModel: @Composable (NavBackStackEntry) -> TicketDetailViewModel,
    modifier: Modifier = Modifier,
) {
    composable(
        route = "detail",
        arguments = MainDestination.TicketDetail.arguments,
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

    val snackbarHostState = remember { SnackbarHostState() }
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val ticketInfoHeight = 125.dp
    var contentWidth by remember { mutableFloatStateOf(0f) }
    var ticketSectionHeight by remember { mutableFloatStateOf(0f) }
    var ticketSectionHeightUntilTicketInfo by remember { mutableFloatStateOf(0f) }
    val bottomAreaHeight =
        ticketSectionHeight - ticketSectionHeightUntilTicketInfo + ticketInfoHeight.toPx()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val managerCodeState by viewModel.managerCodeState.collectAsStateWithLifecycle()
    val ticketGroup = uiState.ticketGroup
    val pagerState = rememberPagerState { ticketGroup.tickets.size }
    val currentTicket = remember(ticketGroup, pagerState.currentPage) {
        ticketGroup.tickets.getOrElse(pagerState.currentPage) { TicketGroup.Ticket() }
    }

    val pullToRefreshState = rememberPullToRefreshState()

    val entranceSuccessMsg = stringResource(R.string.message_ticket_validated)
    LaunchedEffect(viewModel.event) {
        viewModel.event.collect {
            when (it) {
                TicketDetailEvent.ManagerCodeValid -> snackbarHostState.showSnackbar(
                    entranceSuccessMsg
                )

                TicketDetailEvent.OnRefresh -> showEnterCodeDialog = false
                TicketDetailEvent.NotFound -> showTicketNotFoundDialog = true
            }
        }
    }

    Scaffold(
        topBar = {
            BtBackAppBar(
                title = stringResource(R.string.ticket_detail_title),
                onClickBack = onBackClicked,
            )
        },
        snackbarHost = {
            ToastSnackbarHost(
                modifier = Modifier.padding(bottom = 54.dp),
                hostState = snackbarHostState,
            )
        }
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
                    bottomAreaHeight = bottomAreaHeight,
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
                        AsyncImage(
                            model = asyncImageBlurModel(context, ticketGroup.poster, radius = 24),
                            modifier = Modifier
                                .size(
                                    width = contentWidth.toDp(),
                                    height = ticketSectionHeightUntilTicketInfo.toDp(),
                                )
                                .alpha(.8f),
                            contentScale = ContentScale.Crop,
                            contentDescription = null,
                        )
                        // 배경 블러된 이미지 위에 올라가는 그라데이션 배경
                        Box(
                            modifier = Modifier
                                .size(
                                    width = contentWidth.toDp(),
                                    height = ticketSectionHeightUntilTicketInfo.toDp(),
                                )
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            Color(0x33C5CACD),
                                            Grey95.copy(alpha = .2f)
                                        ),
                                        start = Offset.Zero,
                                        end = Offset(
                                            x = contentWidth,
                                            y = ticketSectionHeightUntilTicketInfo
                                        ),
                                    ),
                                )
                        )
                        Column(
                            modifier = Modifier
                                .onGloballyPositioned { coordinates ->
                                    ticketSectionHeightUntilTicketInfo =
                                        coordinates.size.height.toFloat()
                                }
                        ) {
                            Title(showName = ticketGroup.showName)

                            QrCodes(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(marginHorizontal),
                                ticketGroup = ticketGroup,
                                pagerState = pagerState,
                                onClickQr = onClickQr,
                            )

                            DottedDivider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = marginHorizontal),
                                color = White.copy(alpha = 0.3f),
                                thickness = 2.dp
                            )

                            TicketInfo(
                                bottomAreaHeight = ticketInfoHeight,
                                showName = ticketGroup.showName,
                                showDate = ticketGroup.showDate,
                                placeName = ticketGroup.placeName,
                                entryCode = currentTicket.entryCode,
                                ticketState = currentTicket.ticketState,
                                onClickQr = onClickQr,
                            )
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

                    Notice(notice = ticketGroup.ticketNotice)

                    val copiedMessage = stringResource(id = R.string.ticketing_address_copied_message)
                    Inquiry(
                        hostName = ticketGroup.hostName,
                        hostPhoneNumber = ticketGroup.hostPhoneNumber,
                        onClickCopyPlace = {
                            clipboardManager.setText(AnnotatedString(ticketGroup.streetAddress))
                            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                                scope.launch {
                                    snackbarHostState.showSnackbar(copiedMessage)
                                }
                            }
                        },
                        onClickNavigateToShowDetail = {
                            navigateToShowDetail(ticketGroup.showId)
                        }
                    )
                }

                Spacer(modifier = Modifier.size(20.dp))
                RefundPolicySection(uiState.refundPolicy)

                if (currentTicket.ticketState == TicketState.Ready) {
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
            }
            PullToRefreshContainer(
                modifier = Modifier.align(Alignment.TopCenter),
                state = pullToRefreshState,
            )
        }
    }

    if (showEnterCodeDialog) {
        if (LocalDate.now().toEpochDay() < uiState.ticketGroup.showDate.toLocalDate().toEpochDay()) {
            // 아직 공연일 아님
            BTDialog(
                showCloseButton = false,
                onClickPositiveButton = { showEnterCodeDialog = false },
                onDismiss = { showEnterCodeDialog = false },
            ) {
                Text(
                    text = stringResource(R.string.error_show_not_today),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else if (currentTicket.ticketState == TicketState.Ready) {
            ManagerCodeDialog(
                managerCode = managerCodeState.code,
                onManagerCodeChanged = viewModel::setManagerCode,
                error = managerCodeState.error,
                onDismiss = { showEnterCodeDialog = false },
                onClickConfirm = { viewModel.requestEntrance(it) }
            )
        }
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
}

@Composable
private fun Title(showName: String = "") {
    Row(
        modifier = Modifier
            .background(White.copy(alpha = 0.3f))
            .alpha(0.65f)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = showName,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = Grey80,
        )
        Image(
            modifier = Modifier.padding(end = 4.dp),
            painter = painterResource(R.drawable.ic_logo),
            colorFilter = ColorFilter.tint(Grey70.copy(alpha = 0.5f)),
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
                style = MaterialTheme.typography.titleLarge,
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
                    TicketState.Used -> QrCoverView(MaterialTheme.colorScheme.primary, "입장 완료")
                    TicketState.Finished -> QrCoverView(Grey80, "공연 종료")
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
private fun BoxScope.QrCoverView(
    color: Color,
    text: String,
) {
    Box(
        modifier = Modifier.Companion
            .matchParentSize()
            .background(White.copy(0.9f)),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                modifier = Modifier.size(42.dp),
                imageVector = ImageVector.vectorResource(R.drawable.ic_logo),
                tint = color,
                contentDescription = null,
            )
            Text(
                text = text,
                style = point4,
                fontSize = 20.sp,
                color = color,
            )
        }
    }
}

@Composable
private fun TicketInfo(
    bottomAreaHeight: Dp,
    showName: String,
    showDate: LocalDateTime,
    placeName: String,
    entryCode: String,
    ticketState: TicketState,
    onClickQr: () -> Unit,
) {
    Row(
        modifier = Modifier
            .height(bottomAreaHeight)
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.background.copy(alpha = .2f),
                        MaterialTheme.colorScheme.background
                    ),
                )
            )
            .padding(marginHorizontal),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = showName,
                style = point2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onPrimary,
            )
            Row(
                modifier = Modifier.padding(top = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {

                Text(
                    text = showDate.format("yyyy.MM.dd (${showDate.dayOfWeekString})"),
                    style = MaterialTheme.typography.bodySmall,
                    color = Grey30,
                )
                Box(
                    modifier = Modifier
                        .padding(horizontal = 6.dp)
                        .size(width = 1.dp, height = 13.dp)
                        .background(Grey50),
                )
                Text(
                    text = placeName,
                    style = MaterialTheme.typography.bodySmall,
                    color = Grey30,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Spacer(modifier = Modifier.padding(12.dp))
        TicketQr(entryCode, ticketState, onClickQr)
    }
}

@Composable
private fun TicketQr(
    entryCode: String,
    ticketState: TicketState,
    onClickQr: () -> Unit,
) {
    if (entryCode.isBlank()) return

    Box(
        modifier = Modifier,
        contentAlignment = Alignment.Center,
    ) {
        Image(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(White)
                .padding(2.dp)
                .clickable {
                    if (ticketState == TicketState.Ready) onClickQr()
                },
            painter = rememberQrBitmapPainter(
                entryCode,
                size = 66.dp,
            ),
            contentScale = ContentScale.Inside,
            contentDescription = stringResource(R.string.description_qr),
        )
        if (ticketState != TicketState.Ready) {
            val color = when (ticketState) {
                TicketState.Used -> MaterialTheme.colorScheme.primary
                TicketState.Finished -> Grey40
                else -> Grey40
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .size(70.dp)
                    .background(
                        brush = SolidColor(Color.Black),
                        alpha = 0.8f,
                    )
            )
            Text(
                modifier = Modifier
                    .graphicsLayer(rotationZ = -16f)
                    .border(
                        width = 2.dp,
                        color = color,
                        shape = RoundedCornerShape(8.dp),
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                text = when (ticketState) {
                    TicketState.Used -> stringResource(R.string.ticket_used_state)
                    TicketState.Finished -> stringResource(R.string.ticket_show_finished_state)
                    else -> ""
                },
                style = MaterialTheme.typography.titleMedium,
                color = color,
            )
        }
    }
}

@Composable
private fun Notice(notice: String) {
    Column(
        modifier = Modifier
            .padding(horizontal = marginHorizontal)
            .padding(top = 16.dp, bottom = 20.dp)
            .fillMaxWidth(),
    ) {
        Text(
            text = stringResource(R.string.ticket_notice_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            modifier = Modifier.padding(top = 12.dp),
            text = notice,
            style = MaterialTheme.typography.bodySmall,
            color = Grey50,
        )
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
                    containerColor = Grey20,
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
