package com.nexters.boolti.presentation.screen.show

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nexters.boolti.presentation.component.Show
import com.nexters.boolti.presentation.theme.getMarginHorizontal
import java.time.LocalDate

@Composable
fun ShowScreen(
    modifier: Modifier = Modifier,
    onClickTicketing: () -> Unit,
) {
    val shows = (5 downTo -5).toList()

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        LazyVerticalGrid(
            modifier = Modifier.padding(horizontal = getMarginHorizontal()),
            columns = GridCells.Adaptive(minSize = 150.dp),
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp),
        ) {
            val now = LocalDate.now()
            items(count = shows.size, key = { index -> shows[index] }) { index ->
                val tempDay = now.plusDays(shows[index].toLong())
                Show(openDate = tempDay, showDate = tempDay.plusDays(1))
            }
        }
    }
}
