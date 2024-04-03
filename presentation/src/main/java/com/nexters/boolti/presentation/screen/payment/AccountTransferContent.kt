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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nexters.boolti.domain.model.PaymentType
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
import com.nexters.boolti.presentation.theme.point4
import com.nexters.boolti.presentation.theme.subTextPadding
import java.time.LocalDateTime
import com.nexters.boolti.presentation.screen.payment.LegacyTicketSummarySection as LegacyTicketSummarySection1

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

        HorizontalDivider(
            modifier = Modifier.padding(top = 20.dp),
            thickness = 1.dp,
            color = Grey85
        )

        LegacyTicketSummarySection1(
            modifier = Modifier.padding(top = 24.dp),
            poster = reservation.showImage,
            showName = reservation.showName,
            paymentType = PaymentType.ACCOUNT_TRANSFER,
            ticketCount = reservation.ticketCount,
            totalPrice = reservation.totalAmountPrice,
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
        style = point4,
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
        PaymentInfoRow(
            modifier = Modifier.padding(top = subTextPadding),
            label = stringResource(R.string.account_number),
            value = accountNumber,
        )
        PaymentInfoRow(
            modifier = Modifier.padding(top = subTextPadding),
            label = stringResource(R.string.account_holder),
            value = accountHolder,
        )
        PaymentInfoRow(
            modifier = Modifier.padding(top = subTextPadding),
            label = stringResource(R.string.account_transfer_due_date),
            value = dueDate.format("yyyy.MM.dd HH:mm"),
        )
    }
}

@Composable
private fun PaymentInfoRow(label: String, value: String, modifier: Modifier = Modifier) {
    Row(modifier) {
        Text(
            text = label,
            color = Grey30,
        )
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp),
            text = value,
            color = Grey15,
            textAlign = TextAlign.End,
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
