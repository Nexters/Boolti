package com.nexters.boolti.presentation.screen.show

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexters.boolti.domain.model.ShowDetail
import com.nexters.boolti.domain.model.ShowState
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.MainButton
import com.nexters.boolti.presentation.component.ToastSnackbarHost
import com.nexters.boolti.presentation.screen.ticketing.ChooseTicketBottomSheetContent
import com.nexters.boolti.presentation.theme.Grey05
import com.nexters.boolti.presentation.theme.Grey15
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.Grey70
import com.nexters.boolti.presentation.theme.Grey80
import com.nexters.boolti.presentation.theme.Grey85
import com.nexters.boolti.presentation.theme.aggroFamily
import com.nexters.boolti.presentation.theme.marginHorizontal
import com.nexters.boolti.presentation.util.requireActivity
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowDetailScreen(
    onBack: () -> Unit,
    onClickHome: () -> Unit,
    onTicketSelected: (ticketId: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ShowDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = sheetState)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val window = LocalContext.current.requireActivity().window
    window.statusBarColor = MaterialTheme.colorScheme.surface.toArgb()

    BottomSheetScaffold(
        modifier = modifier,
        scaffoldState = scaffoldState,
        snackbarHost = {
            ToastSnackbarHost(
                modifier = Modifier.padding(bottom = 80.dp),
                hostState = snackbarHostState,
            )
        },
        topBar = { ShowDetailAppBar(onBack = onBack, onClickHome = onClickHome) },
        sheetContent = {
            ChooseTicketBottomSheetContent(
                ticketingTickets = uiState.tickets, leftAmount = uiState.leftAmount
            ) { ticket ->
                Timber.tag("MANGBAAM-(TicketScreen)").d("선택된 티켓: $ticket")
                onTicketSelected(ticket.id)
                scope.launch { scaffoldState.bottomSheetState.hide() }
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        sheetContainerColor = MaterialTheme.colorScheme.surfaceTint,
        sheetDragHandle = {
            BottomSheetDefaults.DragHandle(
                shape = RoundedCornerShape(100.dp), width = 45.dp, height = 4.dp, color = Grey70
            )
        },
        sheetPeekHeight = 0.dp,
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
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
                    title = uiState.showDetail.name,
                    images = uiState.showDetail.images.map { it.originImage }
                )
                ContentScaffold(
                    modifier = Modifier
                        .padding(horizontal = marginHorizontal)
                        .padding(bottom = 114.dp),
                    snackbarHost = snackbarHostState,
                    showDetail = uiState.showDetail,
                    host = "김불다람쥐 (010-1234-5678)",
                )
            }

            Column(
                modifier = Modifier.fillMaxHeight()
            ) {
                Spacer(modifier = Modifier.weight(1.0f))
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    MaterialTheme.colorScheme.background,
                                )
                            )
                        )
                )
                ShowDetailCtaButton(
                    showState = ShowState.TicketingInProgress,
                    purchased = uiState.purchased,
                    showMessage = { message ->
                        scope.launch {
                            snackbarHostState.showSnackbar(message = message)
                        }
                    },
                    onClick = { scope.launch { scaffoldState.bottomSheetState.expand() } },
                )
            }
        }
    }
}

@Composable
private fun ShowDetailAppBar(
    onBack: () -> Unit,
    onClickHome: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .height(44.dp)
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surface),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            modifier = Modifier.size(width = 48.dp, height = 44.dp),
            onClick = onBack,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_back),
                contentDescription = stringResource(id = R.string.description_navigate_back),
                Modifier
                    .padding(start = marginHorizontal)
                    .size(width = 24.dp, height = 24.dp)
            )
        }
        IconButton(
            modifier = Modifier.size(width = 64.dp, height = 44.dp),
            onClick = onClickHome,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_home),
                contentDescription = stringResource(id = R.string.description_toolbar_home),
                Modifier.size(width = 24.dp, height = 24.dp)
            )
        }
        Spacer(modifier = Modifier.weight(1.0f))
        IconButton(
            modifier = Modifier.size(width = 64.dp, height = 44.dp),
            onClick = {},
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_share),
                contentDescription = stringResource(id = R.string.ticketing_share),
                Modifier.size(width = 24.dp, height = 24.dp)
            )
        }
        IconButton(
            modifier = Modifier.size(width = 64.dp, height = 44.dp),
            onClick = {},
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_verticle_more),
                contentDescription = stringResource(id = R.string.description_more_menu),
                Modifier.size(width = 24.dp, height = 24.dp)
            )
        }
    }
}

@Composable
private fun ContentScaffold(
    snackbarHost: SnackbarHostState,
    showDetail: ShowDetail,
    host: String,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()

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
        val indexOfDay = showDetail.date.dayOfWeek.value
        val minute = stringResource(id = R.string.ticketing_minutes)
        // ex. 2024.01.20 (토) / 18:00 (150분)
        val formatter =
            DateTimeFormatter.ofPattern("yyyy.MM.dd (${daysOfWeek[indexOfDay]}) / HH:mm (${showDetail.runningTime}${minute})")
        Section(
            title = { SectionTitle(stringResource(id = R.string.ticketing_datetime)) },
            content = { Text(showDetail.date.format(formatter)) },
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
                        stringResource(id = R.string.ticketing_account_copied_message)
                    Row(
                        modifier = Modifier
                            .clip(shape = RoundedCornerShape(4.dp))
                            .background(color = Grey85)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .clickable {
                                clipboardManager.setText(AnnotatedString(showDetail.placeName))
                                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                                    scope.launch {
                                        snackbarHost.showSnackbar(copiedMessage)
                                    }
                                }
                            }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_copy),
                            contentDescription = stringResource(
                                id = R.string.ticketing_copy_address
                            )
                        )
                        Text(
                            modifier = Modifier.padding(start = 6.dp),
                            text = stringResource(
                                id = R.string.ticketing_copy_address
                            ),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
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
            title = { SectionTitle(stringResource(id = R.string.ticketing_content)) },
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
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(color = MaterialTheme.colorScheme.surfaceTint)
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    text = host,
                    style = MaterialTheme.typography.bodyLarge.copy(color = Grey30),
                )
            },
        )
    }
}

@Composable
private fun Poster(
    images: List<String>,
    title: String,
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
        )
        Text(
            modifier = Modifier.padding(top = 24.dp, bottom = 30.dp),
            text = title,
            fontFamily = aggroFamily,
            color = Grey05,
            fontSize = 24.sp,
            lineHeight = 34.sp,
        )
    }
}

@Composable
private fun TicketReservationPeriod(
    startDate: LocalDate,
    endDate: LocalDate,
    modifier: Modifier = Modifier,
) {
    val daysOfWeek = stringArrayResource(id = R.array.days_of_week)
    val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
    val startDayIndex = startDate.dayOfWeek.value
    val endDayIndex = endDate.dayOfWeek.value

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(8.dp))
            .border(shape = RoundedCornerShape(8.dp), color = Color.White, width = 1.dp)
            .background(color = Grey70)
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            stringResource(id = R.string.ticketing_period),
            style = MaterialTheme.typography.titleMedium.copy(color = Grey15)
        )
        Divider(
            modifier = Modifier.padding(vertical = 10.dp), thickness = 1.dp, color = Color.Black
        )
        Text(
            // ex. 2023.12.01 (토) - 2024.01.20 (월)
            "${startDate.format(formatter)} (${daysOfWeek[startDayIndex]}) - " +
                    "${endDate.format(formatter)} (${daysOfWeek[endDayIndex]})",
            style = MaterialTheme.typography.titleMedium.copy(color = Grey30),
        )
    }
}

@Composable
private fun Section(
    title: @Composable () -> Unit, content: @Composable () -> Unit, modifier: Modifier = Modifier
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
    Text(
        modifier = modifier.heightIn(0.dp, 246.dp),
        text = text,
        style = MaterialTheme.typography.bodyLarge.copy(color = Grey30),
        maxLines = maxLines,
        overflow = overflow,
    )
}

@Composable
fun ShowDetailCtaButton(
    onClick: () -> Unit,
    showMessage: (message: String) -> Unit,
    purchased: Boolean,
    showState: ShowState,
    modifier: Modifier = Modifier,
) {
    val enabled = showState is ShowState.TicketingInProgress && !purchased
    val text = when (showState) {
        is ShowState.WaitingTicketing -> stringResource(
            id = R.string.ticketing_button_upcoming_ticket, showState.dDay
        )

        ShowState.TicketingInProgress -> {
            if (purchased) {
                stringResource(id = R.string.ticketing_button_purchased_ticket)
            } else {
                stringResource(id = R.string.ticketing_button_label)
            }
        }

        ShowState.ClosedTicketing -> stringResource(id = R.string.ticketing_button_closed_ticket)
        ShowState.FinishedShow -> stringResource(id = R.string.ticketing_button_finished_show)
    }

    val disabledContentColor =
        if (showState is ShowState.WaitingTicketing) MaterialTheme.colorScheme.primary else Grey50

    MainButton(
        modifier = modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(horizontal = marginHorizontal, vertical = 8.dp)
            .padding(bottom = 34.dp),
        label = text,
        onClick = onClick,
        enabled = enabled,
        disabledContentColor = disabledContentColor,
    )
}