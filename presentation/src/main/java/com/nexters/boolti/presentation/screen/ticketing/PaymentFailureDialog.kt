package com.nexters.boolti.presentation.screen.ticketing

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BTDialog
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.Grey50

@Composable
fun PaymentFailureDialog(modifier: Modifier = Modifier, onClickButton: () -> Unit) {
    BTDialog(
        modifier = modifier,
        enableDismiss = false,
        showCloseButton = false,
        positiveButtonLabel = stringResource(R.string.ticketing_again),
        onClickPositiveButton = onClickButton,
    ) {
        Text(
            text = stringResource(R.string.payment_failed),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            modifier = Modifier.padding(top = 4.dp),
            text = stringResource(R.string.payment_failed_description),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall,
            color = Grey50,
        )
    }
}

@Preview
@Composable
private fun PaymentFailureDialogPreview() {
    BooltiTheme {
        Surface {
            PaymentFailureDialog {}
        }
    }
}
