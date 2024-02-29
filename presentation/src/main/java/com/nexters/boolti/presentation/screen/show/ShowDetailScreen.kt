package com.nexters.boolti.presentation.screen.show

import android.content.Intent
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexters.boolti.domain.model.ShowDetail
import com.nexters.boolti.domain.model.ShowState
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.CopyButton
import com.nexters.boolti.presentation.component.MainButton
import com.nexters.boolti.presentation.component.ToastSnackbarHost
import com.nexters.boolti.presentation.extension.requireActivity
import com.nexters.boolti.presentation.screen.LocalSnackbarController
import com.nexters.boolti.presentation.screen.ticketing.ChooseTicketBottomSheet
import com.nexters.boolti.presentation.theme.Grey20
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.Grey80
import com.nexters.boolti.presentation.theme.Grey85
import com.nexters.boolti.presentation.theme.marginHorizontal
import com.nexters.boolti.presentation.theme.point3
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
    navigateToReport: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ShowDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isLoggedIn by viewModel.loggedIn.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    val window = LocalContext.current.requireActivity().window
    window.statusBarColor = MaterialTheme.colorScheme.surface.toArgb()

    Scaffold(
        modifier = modifier,
        topBar = {
            ShowDetailAppBar(
                onBack = onBack,
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
                    navigateToImages = navigateToImages,
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
                    showState = uiState.showDetail.state,
                    purchased = uiState.showDetail.isReserved,
                    onClick = {
                        scope.launch {
                            if (isLoggedIn == true) {
                                showBottomSheet = true
                            } else {
                                navigateToLogin()
                            }
                        }
                    },
                )
            }
        }
        if (showBottomSheet) {
            ChooseTicketBottomSheet(
                onTicketingClicked = { ticket, count ->
                    Timber.tag("MANGBAAM-(TicketScreen)").d("선택된 티켓: $ticket")
                    onTicketSelected(
                        uiState.showDetail.id,
                        ticket.id,
                        count,
                        ticket.isInviteTicket,
                    )
                    showBottomSheet = false
                },
                onDismissRequest = {
                    showBottomSheet = false
                }
            )
        }
    }
}

@Composable
private fun ShowDetailAppBar(
    onBack: () -> Unit,
    onClickHome: () -> Unit,
    navigateToReport: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var isContextMenuVisible by rememberSaveable {
        mutableStateOf(false)
    }

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
            modifier = Modifier
                .padding(end = 10.dp)
                .size(44.dp),
            onClick = {
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "https://play.google.com/store/apps/details?id=${context.applicationContext.packageName}"
                    )
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(sendIntent, null)
                context.startActivity(shareIntent)
            },
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(R.drawable.ic_share),
                contentDescription = stringResource(id = R.string.ticketing_share),
            )
        }
        IconButton(
            modifier = Modifier
                .padding(end = marginHorizontal)
                .size(24.dp),
            onClick = {
                isContextMenuVisible = true
            },
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(R.drawable.ic_verticle_more),
                contentDescription = stringResource(id = R.string.description_more_menu),
            )
        }
    }

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
                    CopyButton(
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
            .padding(horizontal = marginHorizontal)
            .padding(top = 8.dp, bottom = 20.dp),
        label = text,
        onClick = onClick,
        enabled = enabled,
        disabledContentColor = disabledContentColor,
    )
}
