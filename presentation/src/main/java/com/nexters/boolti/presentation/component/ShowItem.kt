package com.nexters.boolti.presentation.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nexters.boolti.domain.model.Show
import com.nexters.boolti.presentation.screen.payment.TicketSummarySection
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.marginHorizontal
import java.time.LocalDate
import java.time.LocalDateTime

@Composable
fun ShowItem(
    show: Show,
    modifier: Modifier = Modifier,
    colors: CardColors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    paddingValues: PaddingValues = PaddingValues(vertical = 16.dp, horizontal = marginHorizontal),
) {
    TicketSummarySection(
        modifier = modifier,
        poster = show.thumbnailImage,
        showName = show.name,
        showDate = show.date,
        colors = colors,
        paddingValues = paddingValues,
    )
}

@Preview
@Composable
private fun ShowItemPreview() {
    BooltiTheme {
        ShowItem(
            show = Show(
                id = "1",
                name = "2024 TOGETHER LUCKY CLUB",
                thumbnailImage = "",
                date = LocalDateTime.now(),
                salesStartDate = LocalDate.now(),
                salesEndDate = LocalDate.now().plusDays(10),
            ),
        )
    }
}
