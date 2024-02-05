package com.nexters.boolti.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.nexters.boolti.domain.model.Show
import com.nexters.boolti.domain.model.ShowState
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.theme.Grey05
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey80
import com.nexters.boolti.presentation.theme.aggroFamily
import java.time.format.DateTimeFormatter

@Composable
fun ShowFeed(
    show: Show,
    modifier: Modifier = Modifier,
) {
    val borderRadius = 8.dp
    val showState = show.state

    Column(
        modifier = modifier,
    ) {
        Box(
            contentAlignment = Alignment.BottomEnd,
        ) {
            if (showState !is ShowState.TicketingInProgress) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(210f / 297f)
                        .background(
                            brush = SolidColor(Color.Black),
                            alpha = 0.5f,
                        )
                )
            }

            AsyncImage(
                model = show.thumbnailImage,
                contentDescription = stringResource(id = R.string.description_poster),
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(210f / 297f)
                    .clip(RoundedCornerShape(borderRadius))
                    .border(
                        width = 1.dp,
                        color = Grey80,
                        shape = RoundedCornerShape(borderRadius)
                    ),
                contentScale = ContentScale.Crop,
            )

            if (showState is ShowState.WaitingTicketing) {
                Badge(
                    label = stringResource(
                        id = R.string.ticketing_button_upcoming_ticket,
                        showState.dDay
                    ),
                    modifier = Modifier.padding(all = 10.dp),
                    color = Grey05,
                    containerColor = MaterialTheme.colorScheme.primary,
                )
            } else if (showState is ShowState.FinishedShow) Badge(
                label = stringResource(id = R.string.finished_show),
                modifier = Modifier.padding(all = 10.dp)
            )
        }

        val daysOfWeek = stringArrayResource(id = R.array.days_of_week)
        val indexOfDay = show.date.dayOfWeek.value
        val formatter =
            DateTimeFormatter.ofPattern("yyyy.MM.dd (${daysOfWeek[indexOfDay]}) HH:mm")
        Text(
            text = show.date.format(formatter),
            modifier = Modifier.padding(top = 12.dp),
            style = MaterialTheme.typography.bodySmall.copy(color = Grey30)
        )
        Text(
            text = show.name,
            modifier = Modifier.padding(top = 2.dp),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            fontSize = 16.sp,
            lineHeight = 26.sp,
            fontFamily = aggroFamily,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}
