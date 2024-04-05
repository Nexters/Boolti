package com.nexters.boolti.presentation.screen.payment

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nexters.boolti.domain.model.PaymentType
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.extension.showDateTimeString
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.Grey05
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.marginHorizontal
import com.nexters.boolti.presentation.theme.point1
import java.time.LocalDateTime

@Composable
fun TicketSummarySection(
    modifier: Modifier = Modifier,
    poster: String,
    showName: String,
    showDate: LocalDateTime,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(4.dp),
    ) {
        Row(
            modifier = Modifier.padding(vertical = 16.dp, horizontal = marginHorizontal),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = poster,
                contentDescription = stringResource(R.string.description_poster),
                modifier = Modifier
                    .size(width = 70.dp, height = 98.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(4.dp),
                    ),
                contentScale = ContentScale.Crop,
            )
            Column(
                modifier = Modifier.padding(start = 16.dp),
            ) {
                Text(
                    text = showName,
                    style = point1,
                    color = Grey05,
                )
                Text(
                    modifier = Modifier.padding(top = 4.dp),
                    text = showDate.showDateTimeString,
                    color = Grey30,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Composable
fun LegacyTicketSummarySection(
    modifier: Modifier = Modifier,
    poster: String,
    showName: String,
    ticketName: String,
    ticketCount: Int,
    totalPrice: Int,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = poster,
            contentDescription = stringResource(R.string.description_poster),
            modifier = Modifier
                .size(width = 70.dp, height = 98.dp)
                .clip(RoundedCornerShape(4.dp))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(4.dp),
                ),
            contentScale = ContentScale.Crop,
        )
        Column(
            modifier = Modifier.padding(start = 16.dp)
        ) {
            Text(
                text = showName,
                style = point1,
                color = Grey05,
            )

            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = stringResource(
                    id = R.string.reservation_ticket_info_format,
                    ticketName,
                    ticketCount
                ),
                color = Grey30,
                style = MaterialTheme.typography.bodySmall,
            )

            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = stringResource(id = R.string.unit_won, totalPrice),
                color = Grey30,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Preview
@Composable
private fun TicketSummaryPreview() {
    BooltiTheme {
        Surface {
            TicketSummarySection(
                modifier = Modifier.padding(16.dp),
                poster = "",
                showName = "2024 TOGETHER LUCKY CLUB",
                showDate = LocalDateTime.now(),
            )
        }
    }
}

@Preview
@Composable
private fun LegacyTicketSummaryPreview() {
    BooltiTheme {
        Surface {
            LegacyTicketSummarySection(
                modifier = Modifier.padding(24.dp),
                poster = "",
                showName = "2024 TOGETHER LUCKY CLUB",
                ticketName = "일반 티켓 B",
                ticketCount = 10,
                totalPrice = 30000,
            )
        }
    }
}
