package com.nexters.boolti.presentation.screen.ticket

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun TicketScreen(
    modifier: Modifier = Modifier,
    onClickTicket: (String) -> Unit,
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(30) {
            Box(
                modifier = Modifier
                    .aspectRatio(1.3F)
                    .background(Color.LightGray)
                    .clickable { onClickTicket(it.toString()) }
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = "${it + 1}번 공연 티켓",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.Black
                )
            }
        }
    }
}
