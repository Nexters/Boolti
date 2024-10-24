package com.nexters.boolti.presentation.screen.qr

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.nexters.boolti.domain.model.TicketState
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.InstagramIndicator
import com.nexters.boolti.presentation.screen.MainDestination
import com.nexters.boolti.presentation.screen.ticket.detail.TicketDetailViewModel
import com.nexters.boolti.presentation.theme.Grey10
import com.nexters.boolti.presentation.theme.Grey20
import com.nexters.boolti.presentation.theme.Grey60
import com.nexters.boolti.presentation.theme.Grey70
import com.nexters.boolti.presentation.theme.Grey90
import com.nexters.boolti.presentation.theme.point4
import com.nexters.boolti.presentation.util.rememberQrBitmapPainter

fun NavGraphBuilder.qrFullScreen(
    navController: NavHostController,
    popBackStack: () -> Unit,
    getSharedViewModel: @Composable (NavBackStackEntry) -> TicketDetailViewModel,
    modifier: Modifier = Modifier,
) {
    composable(
        route = MainDestination.Qr.route,
    ) { entry ->
        QrFullScreen(
            modifier = modifier,
            viewModel = getSharedViewModel(entry),
        ) { popBackStack() }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun QrFullScreen(
    modifier: Modifier = Modifier,
    viewModel: TicketDetailViewModel = hiltViewModel(),
    onClose: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(uiState.currentPage) { uiState.ticketGroup.tickets.size }

    Scaffold(
        modifier = modifier,
        topBar = { Toolbar(onClose = onClose) },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding),
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxHeight(),
            ) { page ->
                QrContent(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 55.dp),
                    ticketState = uiState.ticketGroup.tickets[page].ticketState,
                    ticketName = uiState.ticketGroup.ticketName,
                    entryCode = uiState.ticketGroup.tickets[page].entryCode,
                    csTicketId = uiState.ticketGroup.tickets[page].csTicketId,
                )
            }
            if (pagerState.pageCount > 1) {
                InstagramIndicator(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 20.dp),
                    pagerState = pagerState,
                    activeColor = Color.Black,
                    inactiveColor = Grey20,
                )
            }
        }
    }
}

@Composable
fun QrContent(
    modifier: Modifier = Modifier,
    ticketState: TicketState,
    ticketName: String,
    entryCode: String,
    csTicketId: String,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier
                .drawWithCache {
                    val color = Grey10
                    onDrawBehind {
                        drawRoundRect(
                            color = color,
                            cornerRadius = CornerRadius(100.dp.toPx(), 100.dp.toPx()),
                        )
                    }
                }
                .padding(vertical = 4.dp, horizontal = 16.dp),
            text = ticketName,
            style = MaterialTheme.typography.titleMedium,
            color = Grey70,
        )

        Box(
            modifier = Modifier
                .height(IntrinsicSize.Max)
                .width(IntrinsicSize.Max),
        ) {
            Image(
                modifier = Modifier.padding(top = 16.dp),
                painter = rememberQrBitmapPainter(
                    content = entryCode,
                    size = 240.dp,
                ),
                contentScale = ContentScale.Inside,
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
            modifier = Modifier.padding(top = 12.dp),
            text = csTicketId,
            style = MaterialTheme.typography.bodySmall,
            color = Grey70,
        )
    }
}

@Composable
fun BoxScope.QrCoverView(
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
private fun Toolbar(modifier: Modifier = Modifier, onClose: () -> Unit) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp),
    ) {
        IconButton(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 8.dp),
            onClick = onClose
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_close),
                tint = Grey90,
                contentDescription = stringResource(R.string.description_close_button),
            )
        }
    }
}
