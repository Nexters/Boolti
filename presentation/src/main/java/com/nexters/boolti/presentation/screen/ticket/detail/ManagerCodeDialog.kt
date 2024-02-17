package com.nexters.boolti.presentation.screen.ticket.detail

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nexters.boolti.domain.exception.ManagerCodeErrorType
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BTDialog
import com.nexters.boolti.presentation.component.BTTextField
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.Grey60

@Composable
fun ManagerCodeDialog(
    managerCode: String,
    onManagerCodeChanged: (String) -> Unit,
    error: ManagerCodeErrorType? = null,
    onDismiss: () -> Unit,
    onClickConfirm: (managerCode: String) -> Unit,
) {
    BTDialog(
        onDismiss = {
            onManagerCodeChanged("")
            onDismiss()
        },
        onClickPositiveButton = { onClickConfirm(managerCode) },
        positiveButtonEnabled = managerCode.isNotEmpty() && error == null,
    ) {
        Text(
            text = stringResource(R.string.enter_code_dialog_title),
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = stringResource(R.string.enter_code_dialog_desc),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 4.dp),
            style = MaterialTheme.typography.bodySmall,
            color = Grey50,
        )
        BTTextField(
            text = managerCode,
            placeholder = stringResource(R.string.enter_code_dialog_placeholder),
            onValueChanged = {
                onManagerCodeChanged(it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                autoCorrect = false,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(
                onDone = { onClickConfirm(managerCode) }
            ),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                focusedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedBorderColor = MaterialTheme.colorScheme.secondaryContainer,
                focusedPlaceholderColor = Grey60,
                unfocusedPlaceholderColor = Grey60,
                focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
        )
        if (error != null) {
            val message = when (error) {
                ManagerCodeErrorType.Unknown -> stringResource(R.string.message_unknown_error)
                ManagerCodeErrorType.Mismatch -> stringResource(R.string.enter_code_dialog_error_mismatch)
                ManagerCodeErrorType.NotToday -> stringResource(R.string.enter_code_dialog_error_not_today)
            }
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp).align(Alignment.Start),
            )
        }
    }
}
