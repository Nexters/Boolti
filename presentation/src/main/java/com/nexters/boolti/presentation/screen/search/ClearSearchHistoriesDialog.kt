package com.nexters.boolti.presentation.screen.search

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BTDialog

@Composable
fun ClearSearchHistoriesDialog(
    onClickClear: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BTDialog(
        modifier = modifier,
        showCloseButton = false,
        enableDismiss = true,
        onDismiss = onDismiss,
        onClickNegativeButton = onDismiss,
        onClickPositiveButton = onClickClear,
        positiveButtonLabel = stringResource(R.string.btn_delete),
        negativeButtonLabel = stringResource(R.string.cancel),
    ) {
        Text(
            text = stringResource(R.string.search_clear_history_dialog),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
        )
    }
}
