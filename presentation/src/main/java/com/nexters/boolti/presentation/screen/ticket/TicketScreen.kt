package com.nexters.boolti.presentation.screen.ticket

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexters.boolti.presentation.component.BtCircularProgressIndicator
import kotlin.math.absoluteValue

@Composable
fun TicketScreen(
    onClickTicket: (String) -> Unit,
    onClickQr: (entryCode: String, ticketName: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TicketViewModel = hiltViewModel(),
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.load()
    }

    when {
        uiState.loading -> Box(modifier = Modifier.fillMaxSize()) {
            BtCircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        uiState.tickets.isNotEmpty() -> TicketNotEmptyScreen(
            modifier,
            uiState,
            onClickQr,
            onClickTicket = onClickTicket
        )

        else -> TicketEmptyScreen(modifier)
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun TicketNotEmptyScreen(
    modifier: Modifier,
    uiState: TicketUiState,
    onClickQr: (entryCode: String, ticketName: String) -> Unit,
    onClickTicket: (ticketId: String) -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val pagerState = rememberPagerState(
            pageCount = { uiState.tickets.size }
        )

        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp

        val contentPadding = 30.dp
        val pageSpacing = 16.dp
        val scaleSizeRatio = 0.8f

        HorizontalPager(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
                .weight(1F),
            state = pagerState,
            contentPadding = PaddingValues(horizontal = contentPadding),
            pageSpacing = pageSpacing,
            beyondBoundsPageCount = 3,
        ) { page ->

            Card(
                Modifier
                    .fillMaxSize()
                    .padding(top = 36.dp, bottom = 16.dp)
                    .align(Alignment.CenterHorizontally)
                    .graphicsLayer {
                        val pageOffset =
                            ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction)

                        alpha = lerp(
                            start = 0.5f,
                            stop = 1f,
                            fraction = 1f - pageOffset.absoluteValue.coerceIn(0f, 1f),
                        )
                        lerp(
                            start = 1f,
                            stop = scaleSizeRatio,
                            fraction = pageOffset.absoluteValue.coerceIn(0f, 1f),
                        ).let {
                            scaleX = it
                            scaleY = it
                        }
                        translationX = calculateTranslationX(
                            screenWidth = screenWidth,
                            scaleRatio = scaleSizeRatio,
                            contentPadding = contentPadding,
                            pageOffset = pageOffset,
                        ).toPx()
                    }
                    .clickable { onClickTicket(uiState.tickets[page].ticketId) },
                shape = RectangleShape,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
            ) {
                val ticket = uiState.tickets[page]
                TicketContent(
                    ticket = ticket,
                    onClickQr = onClickQr,
                )
            }
        }

        Card(
            modifier = Modifier.padding(bottom = 28.dp),
            shape = RoundedCornerShape(100.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        ) {
            Text(
                text = "${pagerState.currentPage + 1}/${pagerState.pageCount}",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp),
            )
        }
    }
}

private fun calculateTranslationX(
    screenWidth: Int,
    scaleRatio: Float,
    contentPadding: Dp,
    pageOffset: Float,
): Dp {
    val pageFullWidth = screenWidth.dp - contentPadding
    return (((pageFullWidth * (1 - scaleRatio)) / 2) * pageOffset)
}
