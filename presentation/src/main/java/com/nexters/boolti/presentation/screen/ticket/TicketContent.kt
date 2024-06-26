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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
import com.nexters.boolti.presentation.extension.toPx
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey40
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.Grey80
import com.nexters.boolti.presentation.theme.Grey95
import com.nexters.boolti.presentation.theme.marginHorizontal
import com.nexters.boolti.presentation.theme.point2
import com.nexters.boolti.presentation.util.TicketShape
import com.nexters.boolti.presentation.util.asyncImageBlurModel
import com.nexters.boolti.presentation.util.rememberQrBitmapPainter
import java.time.LocalDateTime

@Composable
fun Ticket(
    modifier: Modifier = Modifier,
    ticket: Ticket,
    onClick: () -> Unit,
    onClickQr: (data: String, ticketName: String) -> Unit,
) {
    Card(
        modifier = modifier,
        shape = RectangleShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
    ) {
        TicketContent(ticket = ticket, onClick = onClick, onClickQr = onClickQr)
    }
}

@Composable
private fun TicketContent(
    ticket: Ticket,
    onClick: () -> Unit,
    onClickQr: (data: String, ticketName: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val bottomAreaHeight = 125.dp

    var ticketWidth by remember { mutableFloatStateOf(0f) }
    var ticketHeight by remember { mutableFloatStateOf(0f) }

    val ticketShape = TicketShape(
        width = ticketWidth,
        height = ticketHeight,
        circleRadius = 10.dp.toPx(),
        cornerRadius = 8.dp.toPx(),
        bottomAreaHeight = bottomAreaHeight.toPx(),
    )

    Box(
        modifier = modifier
            .onGloballyPositioned { coordinates ->
                ticketWidth = coordinates.size.width.toFloat()
                ticketHeight = coordinates.size.height.toFloat()
            }
            .background(MaterialTheme.colorScheme.background)
            .clip(ticketShape)
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(listOf(White.copy(.5f), White.copy(.2f))),
                shape = ticketShape,
            )
            .clickable(onClick = onClick),
    ) {
        // 배경 블러된 이미지
        AsyncImage(
            model = asyncImageBlurModel(context, ticket.poster, radius = 24),
            modifier = Modifier
                .fillMaxSize()
                .alpha(.8f),
            contentScale = ContentScale.Crop,
            contentDescription = null,
        )
        // 배경 블러된 이미지 위에 올라가는 그라데이션 배경
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0x33C5CACD), Grey95.copy(alpha = .2f)),
                        start = Offset.Zero,
                        end = Offset(ticketWidth, ticketHeight),
                    ),
                )
        )
        // 텍스트 뒤에 깔린 DIM 그라데이션
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(bottomAreaHeight)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Black.copy(alpha = .2f), Black.copy(alpha = .8f))
                    )
                ),
        )
        Column {
            Title(ticket.ticketName, ticket.csTicketId)
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 20.dp, start = 20.dp, end = 20.dp)
                    .clip(RoundedCornerShape(8.dp)),
                model = ticket.poster,
                contentScale = ContentScale.Crop,
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
                onClickQr = { onClickQr(it, ticket.ticketName) },
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
    csTicketId: String = "",
) {
    Row(
        modifier = Modifier
            .background(White.copy(alpha = 0.3f))
            .alpha(0.65f)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier
                .padding(end = 4.dp)
                .size(20.dp),
            painter = painterResource(R.drawable.ic_logo),
            tint = Grey80,
            contentDescription = null,
        )
        Text(
            modifier = Modifier.weight(1f),
            text = ticketName,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = Grey80,
        )
        Text(
            text = csTicketId,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = Grey80,
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
                        brush = SolidColor(Black),
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
