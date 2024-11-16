package com.nexters.boolti.presentation.screen.showdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nexters.boolti.domain.model.ShowState
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.MainButton
import com.nexters.boolti.presentation.component.MainButtonDefaults
import com.nexters.boolti.presentation.extension.asString
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.Grey80
import com.nexters.boolti.presentation.theme.marginHorizontal
import java.time.Duration
import java.time.LocalDateTime

@Composable
fun ShowDetailButtons(
    showState: ShowState,
    onTicketingClicked: () -> Unit,
    onGiftClicked: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxHeight()
    ) {
        Spacer(modifier = Modifier.weight(1.0f))
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.background,
                        )
                    )
                )
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.background)
                .padding(horizontal = marginHorizontal)
                .padding(top = 8.dp, bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(9.dp),
        ) {
            if (showState == ShowState.TicketingInProgress) {
                GiftButton(
                    modifier = Modifier.weight(1f),
                    onClick = onGiftClicked
                )
            }
            TicketingButton(
                modifier = Modifier.weight(1f),
                showState = showState,
                onClick = onTicketingClicked,
            )
        }
    }
}

@Composable
private fun GiftButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MainButton(
        modifier = modifier,
        label = stringResource(R.string.ticketing_give_a_gift),
        onClick = onClick,
        colors = MainButtonDefaults.buttonColors(containerColor = Grey80),
    )
}

@Composable
private fun TicketingButton(
    onClick: () -> Unit,
    showState: ShowState,
    modifier: Modifier = Modifier,
) {
    val enabled = showState is ShowState.TicketingInProgress
    val text = when (showState) {
        is ShowState.WaitingTicketing -> {
            val days = showState.remainingTime.toDays()

            stringResource(
                id = R.string.ticketing_button_ticket_countdown,
                days
            ) + " " + showState.remainingTime.asString()
        }

        ShowState.TicketingInProgress -> stringResource(id = R.string.ticketing_button_label)
        ShowState.ClosedTicketing -> stringResource(id = R.string.ticketing_button_closed_ticket)
        ShowState.FinishedShow -> stringResource(id = R.string.ticketing_button_finished_show)
    }

    val disabledContentColor =
        if (showState is ShowState.WaitingTicketing) MaterialTheme.colorScheme.primary else Grey50

    MainButton(
        modifier = modifier,
        label = text,
        onClick = onClick,
        enabled = enabled,
        colors = MainButtonDefaults.buttonColors(disabledContentColor = disabledContentColor),
    )
}

@Preview(heightDp = 100)
@Composable
fun ShowDetailButtonsPreview() {
    BooltiTheme {
        ShowDetailButtons(
            showState = ShowState.TicketingInProgress,
            onTicketingClicked = {},
            onGiftClicked = {}
        )
    }
}

@Preview(heightDp = 100)
@Composable
fun ShowDetailButtonsBeforeTicketingPreview() {
    BooltiTheme {
        ShowDetailButtons(
            showState = ShowState.WaitingTicketing(
                Duration.ofSeconds(1 * 86400 + 2 * 3600 + 17)
            ),
            onTicketingClicked = {},
            onGiftClicked = {}
        )
    }
}