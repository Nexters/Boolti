package com.nexters.boolti.presentation.screen.ticket.detail

import android.os.Build
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.nexters.boolti.domain.model.TicketState
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.DottedDivider
import com.nexters.boolti.presentation.component.ToastSnackbarHost
import com.nexters.boolti.presentation.extension.dayOfWeekString
import com.nexters.boolti.presentation.extension.format
import com.nexters.boolti.presentation.extension.toDp
import com.nexters.boolti.presentation.extension.toPx
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.Grey20
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey40
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.Grey70
import com.nexters.boolti.presentation.theme.Grey80
import com.nexters.boolti.presentation.theme.Grey95
import com.nexters.boolti.presentation.theme.aggroFamily
import com.nexters.boolti.presentation.theme.marginHorizontal
import com.nexters.boolti.presentation.util.TicketShape
import com.nexters.boolti.presentation.util.asyncImageBlurModel
import com.nexters.boolti.presentation.util.rememberQrBitmapPainter
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketDetailScreen(
    modifier: Modifier = Modifier,
    viewModel: TicketDetailViewModel = hiltViewModel(),
    onBackClicked: () -> Unit,
    onClickQr: (entryCode: String) -> Unit,
    navigateToShowDetail: (showId: String) -> Unit,
) {
    val scrollState = rememberScrollState()
    var showEnterCodeDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val ticketInfoHeight = 125.dp
    var contentWidth by remember { mutableFloatStateOf(0f) }
    var ticketSectionHeight by remember { mutableFloatStateOf(0f) }
    var ticketSectionHeightUntilTicketInfo by remember { mutableFloatStateOf(0f) }
    val bottomAreaHeight = ticketSectionHeight - ticketSectionHeightUntilTicketInfo + ticketInfoHeight.toPx()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val managerCodeState by viewModel.managerCodeState.collectAsStateWithLifecycle()
    val ticket = uiState.ticket

    val pullToRefreshState = rememberPullToRefreshState()

    val entranceSuccessMsg = stringResource(R.string.entry_code_validated)
    LaunchedEffect(viewModel.event) {
        viewModel.event.collect {
            when (it) {
                TicketDetailEvent.ManagerCodeValid -> snackbarHostState.showSnackbar(entranceSuccessMsg)
                TicketDetailEvent.OnRefresh -> showEnterCodeDialog = false
            }
        }
    }

    Scaffold(
        topBar = { TicketDetailToolbar(onBackClicked = onBackClicked) },
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

        Box(modifier = Modifier.nestedScroll(pullToRefreshState.nestedScrollConnection)) {
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
                        .border(1.dp, color = White.copy(alpha = .3f), shape = ticketShape),
                ) {
                    Box {
                        // 배경 블러된 이미지
                        AsyncImage(
                            model = asyncImageBlurModel(context, ticket.poster, radius = 24),
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
                                        colors = listOf(Color(0x33C5CACD), Grey95.copy(alpha = .2f)),
                                        start = Offset.Zero,
                                        end = Offset(x = contentWidth, y = ticketSectionHeightUntilTicketInfo),
                                    ),
                                )
                        )
                        Column(
                            modifier = Modifier
                                .onGloballyPositioned { coordinates ->
                                    ticketSectionHeightUntilTicketInfo = coordinates.size.height.toFloat()
                                }
                        ) {
                            Title(ticketName = ticket.ticketName)

                            AsyncImage(
                                modifier = Modifier
                                    .padding(marginHorizontal)
                                    .aspectRatio(0.75f)
                                    .clip(RoundedCornerShape(8.dp)),
                                model = ticket.poster,
                                contentScale = ContentScale.Crop,
                                contentDescription = stringResource(R.string.description_poster)
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
                                showName = ticket.showName,
                                showDate = ticket.showDate,
                                placeName = ticket.placeName,
                                entryCode = ticket.entryCode,
                                ticketState = ticket.ticketState,
                                onClickQr = onClickQr,
                            )
                        }
                    }

                    Notice(notice = ticket.notice)

                    val copiedMessage = stringResource(id = R.string.ticketing_address_copied_message)
                    Inquiry(
                        hostName = ticket.hostName,
                        hostPhoneNumber = ticket.hostPhoneNumber,
                        onClickCopyPlace = {
                            clipboardManager.setText(AnnotatedString(ticket.streetAddress + ticket.detailAddress))
                            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                                scope.launch {
                                    snackbarHostState.showSnackbar(copiedMessage)
                                }
                            }
                        },
                        onClickNavigateToShowDetail = {
                            navigateToShowDetail(ticket.showId)
                        }
                    )
                }

                Spacer(modifier = Modifier.size(20.dp))

                RefundPolicySection()

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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TicketDetailToolbar(
    onBackClicked: () -> Unit,
) {
    TopAppBar(
        title = {},
        navigationIcon = {
            IconButton(onClick = onBackClicked) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_back),
                    contentDescription = stringResource(R.string.description_navigate_back),
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
    )
}

@Composable
private fun Title(
    ticketName: String = "",
) {
    Row(
        modifier = Modifier
            .background(White.copy(alpha = 0.3f))
            .padding(horizontal = 20.dp, vertical = 10.dp),
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = ticketName,
            style = MaterialTheme.typography.bodySmall,
            color = Grey80,
        )
        Image(
            painter = painterResource(R.drawable.ic_logo),
            colorFilter = ColorFilter.tint(Grey20),
            contentDescription = null,
        )
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
    onClickQr: (entryCode: String) -> Unit,
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
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontFamily = aggroFamily,
                ),
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
    onClickQr: (entryCode: String) -> Unit,
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
                .background(Color.White)
                .padding(2.dp)
                .clickable {
                    if (ticketState == TicketState.Ready) onClickQr(entryCode)
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
fun Inquiry(
    hostName: String,
    hostPhoneNumber: String,
    onClickCopyPlace: () -> Unit,
    onClickNavigateToShowDetail: () -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(horizontal = marginHorizontal)
            .padding(top = 16.dp, bottom = 24.dp)
            .fillMaxWidth(),
    ) {
        Text(
            text = stringResource(R.string.ticket_show_inquiry_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(
            modifier = Modifier.padding(top = 12.dp, bottom = 20.dp),
        ) {
            Text(
                text = stringResource(R.string.ticketing_host),
                style = MaterialTheme.typography.bodySmall,
                color = Grey50,
            )

            Spacer(modifier = Modifier.size(16.dp))

            val hostInfo = String.format("%s (%s)", hostName, hostPhoneNumber)
            Text(
                text = hostInfo,
                style = MaterialTheme.typography.bodySmall,
                color = Grey50,
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
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
                Text(text = stringResource(R.string.copy_show_place_button))
            }
            TextButton(
                modifier = Modifier.weight(1f),
                onClick = onClickNavigateToShowDetail,
                colors = ButtonDefaults.textButtonColors(
                    containerColor = Grey20,
                    contentColor = MaterialTheme.colorScheme.surface,
                ),
                contentPadding = PaddingValues(vertical = 13.dp),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(text = stringResource(R.string.navigate_to_show_button))
            }
        }
    }
}

@Composable
private fun RefundPolicySection() {
    var expanded by remember { mutableStateOf(false) }
    val refundPolicy = stringArrayResource(R.array.refund_policy)
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 0F else 180F,
        animationSpec = tween(),
        label = "expandIconRotation"
    )
    Section(
        title = stringResource(R.string.ticketing_refund_policy_label),
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
                )
        ) {
            if (expanded) {
                refundPolicy.forEach {
                    Row {
                        Text(
                            text = stringResource(R.string.bullet),
                            style = MaterialTheme.typography.bodySmall,
                            color = Grey50,
                        )
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = Grey50,
                            lineHeight = 22.sp,
                        )
                    }
                    Spacer(modifier = Modifier.size(6.dp))
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
            TicketDetailScreen(modifier = Modifier, onBackClicked = {}, onClickQr = {}, navigateToShowDetail = {})
        }
    }
}
