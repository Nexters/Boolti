package com.nexters.boolti.presentation.screen.payment

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.theme.Grey05
import com.nexters.boolti.presentation.theme.Grey30

@Composable
fun TicketSummarySection(
    modifier: Modifier = Modifier,
    poster: String,
    showName: String,
    ticketName: String,
    ticketCount: Int,
    price: Int,
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
            modifier = Modifier.padding(start = 16.dp),
        ) {
            Text(
                text = showName,
                style = MaterialTheme.typography.titleLarge,
                color = Grey05,
            )
            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = "$ticketName / ${stringResource(R.string.ticket_count, ticketCount)}",
                style = MaterialTheme.typography.labelMedium,
                color = Grey30,
            )
            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = stringResource(R.string.unit_won, price),
                style = MaterialTheme.typography.labelMedium,
                color = Grey30,
            )
        }
    }
}
