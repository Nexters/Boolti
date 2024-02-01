package com.nexters.boolti.presentation.screen.show

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.MainButton
import com.nexters.boolti.presentation.screen.ticketing.ChooseTicketBottomSheetContent
import com.nexters.boolti.presentation.theme.Grey05
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey70
import com.nexters.boolti.presentation.theme.aggroFamily
import com.nexters.boolti.presentation.theme.marginHorizontal
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowDetailScreen(
    onTicketSelected: (ticketId: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ShowDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = sheetState)
    val scope = rememberCoroutineScope()

    BottomSheetScaffold(
        modifier = modifier,
        scaffoldState = scaffoldState,
        topBar = { ShowDetailAppBar() },
        sheetContent = {
            ChooseTicketBottomSheetContent(
                ticketingTickets = uiState.tickets,
                leftAmount = uiState.leftAmount
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
                shape = RoundedCornerShape(100.dp),
                width = 45.dp,
                height = 4.dp,
                color = Grey70
            )
        },
        sheetPeekHeight = 0.dp,
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            val scrollState = rememberScrollState()
            Column(modifier = Modifier.verticalScroll(scrollState)) {
                Poster(
                    modifier = modifier.fillMaxWidth(),
                    title = "2024 TOGETHER LUCKY CLUB",
                    images = listOf("https://picsum.photos/400/550")
                )
                TicketReservationPeriod(LocalDateTime.now(), LocalDateTime.now())
                SectionTitle("일시")
                Text("2024.01.20 (토) / 18:00 (150분)")
                Divider()
                SectionTitle("장소")
                Text("클럽 샤프", style = MaterialTheme.typography.bodyLarge)
                SectionContent(text = "서울틀벽시 마포구 와우산로 19길 20 / 지하 1층")
                Divider()
                SectionTitle("공연 내용")
                SectionContent(
                    "[팀명 및 팀 소개]\n\n" +
                            "OvO (오보)\n" +
                            "웃는 표정, 틀려도 웃고 넘기자!\n\n" +
                            "[곡 소개]\n\n" +
                            "The Volunteers - Let me go!\n" +
                            "실리카켈 - No Pain\n" +
                            "데이먼스 이어 - Yours\n" +
                            "윤하 - 오르트구름 (Rock 편곡)\n" +
                            "체리필터 - 낭만고양이",
                    maxLines = 11,
                    overflow = TextOverflow.Ellipsis,
                )
                Divider()
                SectionTitle("주최자")
                Text(text = "김불다람쥐 (010-1234-5678)")
            }

            MainButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = marginHorizontal, vertical = 8.dp)
                    .padding(bottom = 34.dp)
                    .align(Alignment.BottomCenter),
                label = stringResource(id = R.string.ticketing_button_label)
            ) {
                scope.launch {
                    scaffoldState.bottomSheetState.expand()
                }
            }
        }
    }
}

@Composable
private fun ShowDetailAppBar(
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
            onClick = {},
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_back),
                contentDescription = stringResource(id = R.string.description_navigate_back),
                Modifier
                    .padding(start = marginHorizontal)
                    .size(width = 24.dp, height = 24.dp)
            )
        }
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
            .background(color = MaterialTheme.colorScheme.surface)
            .padding(top = 16.dp)
            .padding(horizontal = 38.dp)
    ) {
        AsyncImage(
            modifier = Modifier.fillMaxWidth(),
            model = images.first(),
            contentDescription = "포스터",
            contentScale = ContentScale.FillWidth,
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
    stateTime: LocalDateTime,
    endTime: LocalDateTime,
    modifier: Modifier = Modifier,
) {
    Column {
        Text("티켓 예매 기간")
        Text("2023.12.01 (토) - 2024.01.20 (월)")
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
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.bodyLarge.copy(color = Grey30),
        maxLines = maxLines,
        overflow = overflow,
    )
}