package com.nexters.boolti.presentation.screen.payment

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey85
import com.nexters.boolti.presentation.theme.marginHorizontal

@Composable
fun InviteTicketCompleteScreen(
    modifier: Modifier = Modifier,
    onClickHome: () -> Unit = {},
    onClickClose: () -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            PaymentToolbar(onClickHome = onClickHome, onClickClose = onClickClose)
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = marginHorizontal)
        ) {
            HeaderSection()
            Divider(
                modifier = Modifier.padding(top = 20.dp),
                thickness = 1.dp,
                color = Grey85,
            )
            TicketSummarySection(Modifier.padding(top = 24.dp))
        }
    }
}

@Composable
private fun HeaderSection() {
    Text(
        modifier = Modifier.padding(top = 20.dp),
        text = stringResource(R.string.invite_ticket_complete_title),
        style = MaterialTheme.typography.headlineMedium,
    )
    Text(
        modifier = Modifier.padding(top = 8.dp),
        text = stringResource(R.string.invite_ticket_complete_description),
        style = MaterialTheme.typography.bodySmall,
        color = Grey30,
    )
}
