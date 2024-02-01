package com.nexters.boolti.presentation.screen.payment

import android.content.Context
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.ToastSnackbarHost
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.Grey15
import com.nexters.boolti.presentation.theme.Grey20
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.Grey85
import com.nexters.boolti.presentation.theme.Grey90
import com.nexters.boolti.presentation.theme.marginHorizontal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun AccountTransferScreen(
    modifier: Modifier = Modifier,
    onClickHome: () -> Unit = {},
    onClickClose: () -> Unit = {},
) {
    val clipboardManager = LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val dueDate by remember { mutableStateOf("1월 19일") }
    val price by remember { mutableIntStateOf(5000) }
    val accountNumber by remember { mutableStateOf("110-584-112392") }

    Scaffold(
        modifier = modifier.scrollable(rememberScrollState(), Orientation.Vertical),
        topBar = {
            PaymentToolbar(onClickHome = onClickHome, onClickClose = onClickClose)
        },
        snackbarHost = {
            ToastSnackbarHost(hostState = snackbarHostState, modifier = Modifier.padding(bottom = 40.dp))
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = marginHorizontal),
        ) {
            HeaderSection(price, dueDate)
            Divider(
                modifier = Modifier.padding(top = 20.dp),
                thickness = 1.dp,
                color = Grey85,
            )
            TicketSummarySection(Modifier.padding(top = 24.dp))

            Box(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .background(MaterialTheme.colorScheme.surface)
                    .clip(RoundedCornerShape(8.dp))
                    .padding(vertical = 16.dp, horizontal = 20.dp)
            ) {
                PaymentInfoSection(accountNumber)
            }
            AccountNumberCopyButton(clipboardManager, accountNumber, scope, snackbarHostState, context)
        }
    }
}

@Composable
private fun HeaderSection(price: Int, dueDate: String) {
    val priceString = stringResource(R.string.unit_won, price)
    val fullText = StringBuilder(stringResource(R.string.payment_title, dueDate, priceString))
    val spanIndices = buildList {
        add(Pair(fullText.indexOf(dueDate), dueDate.length))
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
        text = "입금 마감일까지 입금이 확인되지 않는 경우\n주문이 자동 취소됩니다.",
        style = MaterialTheme.typography.bodySmall,
        color = Grey30,
    )
}

@Composable
private fun PaymentInfoSection(accountNumber: String) {
    Column {
        PaymentInfoRow("은행명", "신한은행")
        PaymentInfoRow("계좌번호", accountNumber)
        PaymentInfoRow("예금주", "박불티")
        PaymentInfoRow("입금 마감일", "2024.01.19 23:59")
    }
}

@Composable
private fun PaymentInfoRow(label: String, value: String) {
    Row {
        Text(
            modifier = Modifier.weight(100F),
            text = label,
            color = Grey50,
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
    clipboardManager: ClipboardManager,
    accountNumber: String,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    context: Context,
) {
    Button(
        modifier = Modifier
            .padding(top = 16.dp)
            .fillMaxWidth(),
        onClick = {
            clipboardManager.setText(AnnotatedString(accountNumber))
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = context.getString(R.string.account_number_copied_message),
                    )
                }
            }
        },
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Grey20,
            contentColor = Grey90,
        ),
        contentPadding = PaddingValues(13.dp)
    ) {
        Text(text = "계좌번호 복사하기", style = MaterialTheme.typography.titleMedium)
    }
}

@Preview
@Composable
fun AccountTransferScreenPreview() {
    BooltiTheme {
        Surface {
            AccountTransferScreen()
        }
    }
}
