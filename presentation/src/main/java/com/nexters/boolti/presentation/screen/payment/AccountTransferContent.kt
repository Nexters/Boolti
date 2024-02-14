package com.nexters.boolti.presentation.screen.payment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import com.nexters.boolti.domain.model.ReservationDetail
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.extension.format
import com.nexters.boolti.presentation.theme.Grey15
import com.nexters.boolti.presentation.theme.Grey20
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.Grey85
import com.nexters.boolti.presentation.theme.Grey90
import com.nexters.boolti.presentation.theme.marginHorizontal
import java.time.LocalDateTime

@Composable
fun AccountTransferContent(
    modifier: Modifier = Modifier,
    reservation: ReservationDetail,
    onClickCopyAccountNumber: (accountNumber: String) -> Unit,
) {
    Column(
        modifier = modifier.padding(horizontal = marginHorizontal),
    ) {
        HeaderSection(reservation.totalAmountPrice, reservation.salesEndDateTime)
        Divider(
            modifier = Modifier.padding(top = 20.dp),
            thickness = 1.dp,
            color = Grey85,
        )
        TicketSummarySection(
            Modifier.padding(top = 24.dp),
            poster = reservation.showImage,
            showName = reservation.showName,
            ticketName = reservation.ticketName,
            ticketCount = reservation.ticketCount,
            price = reservation.totalAmountPrice,
        )

        Box(
            modifier = Modifier
                .padding(top = 24.dp)
                .background(MaterialTheme.colorScheme.surface)
                .clip(RoundedCornerShape(8.dp))
                .padding(vertical = 16.dp, horizontal = 20.dp)
        ) {
            PaymentInfoSection(
                bankName = reservation.bankName,
                accountNumber = reservation.accountNumber,
                accountHolder = reservation.accountHolder,
                dueDate = reservation.salesEndDateTime,
            )
        }
        AccountNumberCopyButton(reservation.accountNumber, onClickCopyAccountNumber)
    }
}

@Composable
private fun HeaderSection(price: Int, dueDate: LocalDateTime) {
    val dueDateString = stringResource(R.string.date_format_kor, dueDate.monthValue, dueDate.dayOfMonth)
    val priceString = stringResource(R.string.unit_won, price)
    val fullText = StringBuilder(stringResource(R.string.account_transfer_title, dueDateString, priceString))
    val spanIndices = buildList {
        add(Pair(fullText.indexOf(dueDateString), dueDateString.length))
        add(Pair(fullText.indexOf(priceString), priceString.length))
    }

    Text(
        modifier = Modifier.padding(top = 20.dp),
        text = buildAnnotatedString {
            append(fullText)
            spanIndices.forEach { (start, length) ->
                addStyle(
                    SpanStyle(color = MaterialTheme.colorScheme.primary),
                    start,
                    start + length,
                )
            }
        },
        style = MaterialTheme.typography.headlineMedium,
    )
    Text(
        modifier = Modifier.padding(top = 8.dp),
        text = stringResource(R.string.account_transfer_description),
        style = MaterialTheme.typography.bodySmall,
        color = Grey50,
    )
}

@Composable
private fun PaymentInfoSection(
    bankName: String,
    accountNumber: String,
    accountHolder: String,
    dueDate: LocalDateTime,
) {
    Column(
        modifier = Modifier.clip(RoundedCornerShape(4.dp))
    ) {
        PaymentInfoRow(stringResource(R.string.bank_name), bankName)
        PaymentInfoRow(stringResource(R.string.account_number), accountNumber)
        PaymentInfoRow(stringResource(R.string.account_holder), accountHolder)
        PaymentInfoRow(stringResource(R.string.account_transfer_due_date), dueDate.format("yyyy.MM.dd HH:mm"))
    }
}

@Composable
private fun PaymentInfoRow(label: String, value: String) {
    Row {
        Text(
            modifier = Modifier.weight(100F),
            text = label,
            color = Grey30,
        )
        Text(
            modifier = Modifier
                .weight(195F)
                .padding(start = 12.dp),
            text = value,
            color = Grey15,
        )
    }
}


@Composable
private fun AccountNumberCopyButton(
    accountNumber: String,
    onClickCopyAccountNumber: (accountNumber: String) -> Unit,
) {
    Button(
        modifier = Modifier
            .padding(top = 16.dp)
            .fillMaxWidth(),
        onClick = { onClickCopyAccountNumber(accountNumber) },
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Grey20,
            contentColor = Grey90,
        ),
        contentPadding = PaddingValues(13.dp)
    ) {
        Text(
            text = stringResource(R.string.account_number_copy_button),
            style = MaterialTheme.typography.titleMedium,
        )
    }
}
