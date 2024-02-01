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
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = "https://images.khan.co.kr/article/2023/09/12/news-p.v1.20230912.69ec17ff44f14cc28a10fff6e935e41b_P1.png", // TODO 제거
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
                text = "2024 TOGETHER LUCKY CLUB",
                style = MaterialTheme.typography.titleLarge,
                color = Grey05,
            )
            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = "일반 티켓 B / 1매",
                style = MaterialTheme.typography.labelMedium,
                color = Grey30,
            )
            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = "5,000원",
                style = MaterialTheme.typography.labelMedium,
                color = Grey30,
            )
        }
    }
}
