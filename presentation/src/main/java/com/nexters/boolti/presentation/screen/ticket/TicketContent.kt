package com.nexters.boolti.presentation.screen.ticket

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.DottedDivider
import com.nexters.boolti.presentation.theme.Grey20
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey40
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.Grey80
import com.nexters.boolti.presentation.theme.aggroFamily
import com.nexters.boolti.presentation.theme.marginHorizontal
import com.nexters.boolti.presentation.util.asyncImageBlurModel
import com.nexters.boolti.presentation.util.rememberQrBitmapPainter

@Composable
fun TicketContent(
    poster: String,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val ticketState = TicketState.entries.random()

    Box(modifier = modifier) {
        AsyncImage(
            model = asyncImageBlurModel(context, poster),
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.3f),
            contentDescription = null,
            contentScale = ContentScale.Crop,
        )
        Column {
            Row(
                modifier = Modifier
                    .background(White.copy(alpha = 0.3f))
                    .padding(horizontal = 20.dp, vertical = 10.dp),
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = "일반티켓 B • 1매",
                    style = MaterialTheme.typography.bodySmall,
                    color = Grey80,
                )
                Image(
                    painter = painterResource(R.drawable.boolti_logo),
                    colorFilter = ColorFilter.tint(Grey20),
                    contentDescription = null,
                )
            }
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 20.dp, start = 20.dp, end = 20.dp)
                    .clip(RoundedCornerShape(8.dp)),
                model = poster,
                contentScale = ContentScale.FillWidth,
                contentDescription = "포스터",
            )
            DottedDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                thickness = 2.dp,
                color = White.copy(alpha = .3f),
            )
            Row(
                modifier = Modifier
                    .padding(horizontal = marginHorizontal)
                    .padding(bottom = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "2024 TOGETHER LUCKY CLUB2024 TOGETHER LUCKY CLUB2024 TOGETHER LUCKY CLUB2024 TOGETHER LUCKY CLUB",
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
                            text = "2024.01.20 (토)",
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
                            text = "클럽 샤프클럽 샤프클럽 샤프클럽 샤프클럽 샤프클럽 샤프클럽 샤프클럽 샤프",
                            style = MaterialTheme.typography.bodySmall,
                            color = Grey30,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                Spacer(modifier = Modifier.padding(12.dp))
                TicketQr(ticketState)
            }
        }
    }
}

@Composable
fun TicketQr(
    ticketState: TicketState,
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
                .padding(2.dp),
            painter = rememberQrBitmapPainter(
                "im hero",
                size = 66.dp,
            ),
            contentScale = ContentScale.Inside,
            contentDescription = "입장 QR 코드",
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
                text = "입장 완료",
                style = MaterialTheme.typography.titleMedium,
                color = color,
            )
        }
    }
}