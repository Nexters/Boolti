package com.nexters.boolti.presentation.screen.ticket

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nexters.boolti.domain.model.Ticket
import com.nexters.boolti.domain.model.TicketState
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.DottedDivider
import com.nexters.boolti.presentation.extension.dayOfWeekString
import com.nexters.boolti.presentation.extension.format
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey40
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.Grey80
import com.nexters.boolti.presentation.theme.marginHorizontal
import com.nexters.boolti.presentation.theme.point2
import com.nexters.boolti.presentation.util.TicketShape
import com.nexters.boolti.presentation.util.asyncImageBlurModel
import com.nexters.boolti.presentation.util.rememberQrBitmapPainter
import com.nexters.boolti.presentation.util.ticketPath
import java.time.LocalDateTime

@Composable
fun TicketContent(
    ticket: Ticket,
    onClickQr: (data: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val bottomAreaHeight = 125.dp

    var ticketWidth by remember { mutableFloatStateOf(0f) }
    var ticketHeight by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .onGloballyPositioned { coordinates ->
                ticketWidth = coordinates.size.width.toFloat()
                ticketHeight = coordinates.size.height.toFloat()
            }
            .graphicsLayer {
                shape = TicketShape(
                    width = ticketWidth,
                    height = ticketHeight,
                    circleRadius = 10.dp.toPx(),
                    cornerRadius = 8.dp.toPx(),
                    bottomAreaHeight = bottomAreaHeight.toPx(),
                )
                clip = true
            }
            .drawBehind {
                // 보더 Line
                drawPath(
                    brush = Brush.linearGradient(
                        listOf(
                            White.copy(alpha = .5f),
                            White.copy(alpha = .2f),
                        ),
                        start = Offset(ticketWidth / 2, 0f),
                        end = Offset(0f, ticketHeight / 0f),
                    ),
                    path = ticketPath(
                        width = ticketWidth,
                        height = ticketHeight,
                        circleRadius = 10.dp.toPx(),
                        cornerRadius = 8.dp.toPx(),
                        bottomAreaHeight = bottomAreaHeight.toPx(),
                    ),
                    style = Stroke(
                        width = 1.dp.toPx(),
                    )
                )
            }
    ) {
        AsyncImage(
            model = asyncImageBlurModel(context, ticket.poster, radius = 24),
            modifier = Modifier
                .fillMaxSize(),
            contentScale = ContentScale.Crop,
            contentDescription = null,
        )
        // 포스터 위에 올라가는 그라데이션
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xCCC5CACD),
                            Color(0xCC090A0B),
                        ),
                        start = Offset.Zero,
                        end = Offset(ticketWidth, ticketHeight),
                    ),
                    alpha = .8f,
                )
        )
        // 텍스트 뒤에 깔린 DIM 그라데이션
        Box(
            modifier = Modifier
                .fillMaxSize()
                .height(bottomAreaHeight)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Black.copy(alpha = .2f), Black.copy(alpha = .8f))
                    )
                )
        )
        Column {
            Title(ticket.ticketName, 1) // TODO 개수 정보 생기면 업데이트
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 20.dp, start = 20.dp, end = 20.dp)
                    .clip(RoundedCornerShape(8.dp)),
                model = ticket.poster,
                contentScale = ContentScale.FillWidth,
                contentDescription = stringResource(R.string.description_poster),
            )
            DottedDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, start = 20.dp, end = 20.dp),
                thickness = 2.dp,
                color = White.copy(alpha = .3f),
            )
            TicketInfo(
                bottomAreaHeight = bottomAreaHeight,
                showName = ticket.showName,
                showDate = ticket.showDate,
                placeName = ticket.placeName,
                entryCode = ticket.entryCode,
                ticketState = ticket.ticketState,
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
}

@Composable
private fun Title(
    ticketName: String = "",
    count: Int = 0,
) {
    Row(
        modifier = Modifier
            .background(White.copy(alpha = 0.3f))
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = stringResource(R.string.ticket_title, ticketName, count),
            style = MaterialTheme.typography.bodySmall,
            color = Grey80,
        )
        Image(
            painter = painterResource(R.drawable.ic_logo),
            colorFilter = ColorFilter.tint(Grey80),
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
            .padding(horizontal = marginHorizontal),
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
    onClickQr: (entryCode: String) -> Unit,
) {
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
