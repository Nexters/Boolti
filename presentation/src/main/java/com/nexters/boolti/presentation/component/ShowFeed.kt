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
import androidx.compose.ui.graphics.Brush
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
import com.nexters.boolti.presentation.constants.posterRatio
import com.nexters.boolti.presentation.extension.toPx
import com.nexters.boolti.presentation.theme.Grey05
import com.nexters.boolti.presentation.theme.Grey20
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey40
import com.nexters.boolti.presentation.theme.Grey80
import com.nexters.boolti.presentation.theme.Grey95
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
            AsyncImage(
                model = show.thumbnailImage,
                contentDescription = stringResource(id = R.string.description_poster),
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(posterRatio)
                    .clip(RoundedCornerShape(borderRadius))
                    .border(
                        width = 1.dp,
                        color = Grey80,
                        shape = RoundedCornerShape(borderRadius)
                    ),
                contentScale = ContentScale.Crop,
            )

            if (showState is ShowState.WaitingTicketing || showState is ShowState.FinishedShow) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(posterRatio)
                        .background(
                            brush = SolidColor(Color.Black),
                            alpha = 0.5f,
                        )
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(posterRatio)
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(Color.Transparent, Grey95),
                                startY = 48.dp.toPx(),
                            ),
                            alpha = 0.5f,
                        )
                )
            }

            ShowBadge(
                modifier = Modifier.padding(all = 10.dp),
                showState = showState
            )
        }

        val daysOfWeek = stringArrayResource(id = R.array.days_of_week)
        val indexOfDay = show.date.dayOfWeek.value - 1
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

@Composable
private fun ShowBadge(
    showState: ShowState,
    modifier: Modifier = Modifier,
) {
    var dDay: Int? = null
    val (color, containerColor, labelId) = when (showState) {
        is ShowState.WaitingTicketing -> {
            dDay = showState.dDay
            Triple(
                MaterialTheme.colorScheme.primary,
                Grey80,
                R.string.ticketing_button_upcoming_ticket,
            )
        }

        ShowState.TicketingInProgress -> Triple(
            Grey05,
            MaterialTheme.colorScheme.primary,
            R.string.ticketing_in_progress,
        )

        ShowState.ClosedTicketing -> Triple(Grey80, Grey20, R.string.ticketing_button_closed_ticket)
        ShowState.FinishedShow -> Triple(Grey40, Grey80, R.string.finished_show)
    }
    val label = if (dDay == null) stringResource(labelId) else stringResource(labelId, dDay)


    Text(
        text = label,
        modifier = modifier
            .clip(RoundedCornerShape(100.dp))
            .background(containerColor.copy(0.9f))
            .padding(horizontal = 12.dp, vertical = 3.dp),
        style = MaterialTheme.typography.labelMedium.copy(color = color),
    )
}