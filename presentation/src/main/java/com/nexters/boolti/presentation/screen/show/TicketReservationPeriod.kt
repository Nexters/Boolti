package com.nexters.boolti.presentation.screen.show

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.DottedDivider
import com.nexters.boolti.presentation.theme.Grey15
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey70
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun TicketReservationPeriod(
    startDate: LocalDate,
    endDate: LocalDate,
    modifier: Modifier = Modifier,
) {
    val daysOfWeek = stringArrayResource(id = R.array.days_of_week)
    val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
    val startDayIndex = startDate.dayOfWeek.value
    val endDayIndex = endDate.dayOfWeek.value

    var size by remember {
        mutableStateOf(IntSize.Zero)
    }
    val gradientOffset by remember {
        derivedStateOf {
            Offset(
                size.width.toFloat() / 32,
                size.height.toFloat()
            )
        }
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape = ReservationPeriodShape(radius = 8.dp, circleRadius = 10.dp))
            .border(
                shape = ReservationPeriodShape(radius = 8.dp, circleRadius = 10.dp),
                brush = Brush.linearGradient(
                    colorStops = arrayOf(
                        Pair(0.25f, Color(0xFFDBCBCB)),
                        Pair(0.75f, Color.Transparent),
                    ),
                    end = gradientOffset,
                ),
                width = 1.dp,
            )
            .border(
                shape = ReservationPeriodShape(radius = 8.dp, circleRadius = 10.dp),
                brush = Brush.linearGradient(
                    colorStops = arrayOf(
                        Pair(0.25f, Color(0xFFC2C2C2).copy(alpha = 0.6538f)),
                        Pair(0.75f, Color.Transparent),
                    ),
                    start = Offset(size.width.toFloat(), size.height.toFloat()),
                    end = Offset(size.width.toFloat(), size.height.toFloat()) - gradientOffset
                ),
                width = 1.dp,
            )
            .background(color = Grey70)
            .padding(vertical = 12.dp)
            .onSizeChanged { size = it },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(shape = RoundedCornerShape(10.dp))
                    .background(color = Grey30)
            )
            Text(
                modifier = Modifier.padding(horizontal = 8.5.dp),
                text = stringResource(id = R.string.ticketing_period),
                style = MaterialTheme.typography.titleMedium.copy(color = Grey15)
            )
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(shape = RoundedCornerShape(10.dp))
                    .background(color = Grey30)
            )
        }
        DottedDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            color = Color.Black,
            thickness = 1.dp,
            dash = 6.dp,
            spacedBy = 6.dp,
        )
        Text(
            // ex. 2023.12.01 (토) - 2024.01.20 (월)
            "${startDate.format(formatter)} (${daysOfWeek[startDayIndex]}) - " +
                    "${endDate.format(formatter)} (${daysOfWeek[endDayIndex]})",
            style = MaterialTheme.typography.titleMedium.copy(color = Grey30),
        )
    }
}