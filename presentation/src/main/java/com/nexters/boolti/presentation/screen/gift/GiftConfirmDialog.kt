package com.nexters.boolti.presentation.screen.gift

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BTDialog
import com.nexters.boolti.presentation.extension.toContactFormat
import com.nexters.boolti.presentation.theme.Grey15
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.marginHorizontal

@Composable
fun GiftConfirmDialog(
    receiverName: String,
    receiverContact: String,
    senderName: String,
    senderContact: String,
    ticketName: String,
    ticketCount: Int,
    totalPrice: Int,
    onClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    BTDialog(
        positiveButtonLabel = stringResource(R.string.ticketing_payment_button_label_short),
        onClickPositiveButton = onClick,
        onDismiss = onDismiss,
    ) {
        Text(
            text = stringResource(R.string.ticketing_confirm_dialog_title),
            style = MaterialTheme.typography.headlineSmall
        )
        Column(
            modifier = Modifier
                .padding(top = 24.dp)
                .clip(RoundedCornerShape(4.dp))
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(horizontal = marginHorizontal, vertical = 16.dp),
        ) {
            InfoRow(
                label = stringResource(R.string.gift_receiver),
                value1 = receiverName,
                value2 = receiverContact.toContactFormat(),
            )
            InfoRow(
                modifier = Modifier.padding(top = 16.dp),
                label = stringResource(R.string.gift_sender),
                value1 = senderName,
                value2 = senderContact.toContactFormat(),
            )
            InfoRow(
                modifier = Modifier.padding(top = 16.dp),
                label = stringResource(R.string.ticket),
                value1 = ticketName,
                value2 = stringResource(
                    R.string.reservations_ticket_count_price_format_short,
                    ticketCount,
                    totalPrice
                ),
            )
        }
    }
}

@Composable
private fun InfoRow(
    modifier: Modifier = Modifier,
    label: String,
    value1: String,
    value2: String? = null,
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = Grey30)
            Text(text = value1, style = MaterialTheme.typography.bodySmall, color = Grey15)
        }
        value2?.let {
            Text(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .align(AbsoluteAlignment.Right),
                text = value2,
                style = MaterialTheme.typography.bodySmall,
                color = Grey15,
            )
        }
    }
}

@Preview
@Composable
fun GiftConfirmDialogPreview() {
    GiftConfirmDialog(
        receiverName = "박다람쥐",
        receiverContact = "010-8080-9090",
        senderName = "김불티",
        senderContact = "010-1010-2020",
        ticketName = "일반 티켓 B",
        ticketCount = 1,
        totalPrice = 5000,
        onClick = {},
        onDismiss = {},
    )
}