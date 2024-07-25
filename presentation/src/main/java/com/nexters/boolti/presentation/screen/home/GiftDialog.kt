package com.nexters.boolti.presentation.screen.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BTDialog
import com.nexters.boolti.presentation.theme.Grey15
import com.nexters.boolti.presentation.theme.Grey50

@Composable
fun GiftDialog(
    status: GiftStatus,
    onDismiss: () -> Unit,
    receiveGift: () -> Unit,
    requireLogin: () -> Unit,
    onFailed: () -> Unit,
) {
    val buttonTextRes = when (status) {
        GiftStatus.SELF, GiftStatus.CAN_REGISTER -> R.string.gift_register
        GiftStatus.NEED_LOGIN -> R.string.gift_login
        GiftStatus.FAILED -> R.string.description_close_button
    }

    val textRes = when (status) {
        GiftStatus.SELF -> R.string.gift_self_dialog
        GiftStatus.NEED_LOGIN -> R.string.gift_need_login
        GiftStatus.CAN_REGISTER -> R.string.gift_register_dialog
        GiftStatus.FAILED -> R.string.gift_registration_failed
    }

    val action: () -> Unit = when (status) {
        GiftStatus.SELF -> {
            {
                receiveGift()
                onDismiss()
            }
        }

        GiftStatus.NEED_LOGIN -> requireLogin
        GiftStatus.CAN_REGISTER -> {
            {
                receiveGift()
                onDismiss()
            }
        }

        GiftStatus.FAILED -> onDismiss
    }

    val hasNegativeButton = status in listOf(GiftStatus.SELF, GiftStatus.CAN_REGISTER)

    BTDialog(
        onDismiss = onDismiss,
        onClickPositiveButton = action,
        positiveButtonLabel = stringResource(id = buttonTextRes),
        hasNegativeButton = hasNegativeButton,
        onClickNegativeButton = onFailed
    ) {
        Text(
            text = stringResource(id = textRes),
            style = MaterialTheme.typography.titleLarge.copy(
                color = Grey15,
                textAlign = TextAlign.Center
            ),
        )
        if (status == GiftStatus.FAILED) {
            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = stringResource(id = R.string.gift_registration_failed_dialog),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Grey50,
                    textAlign = TextAlign.Center
                ),
            )
        }
    }
}