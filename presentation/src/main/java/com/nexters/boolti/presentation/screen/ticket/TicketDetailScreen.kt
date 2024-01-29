package com.nexters.boolti.presentation.screen.ticket

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.nexters.boolti.presentation.component.BTDialog
import com.nexters.boolti.presentation.component.BTTextField
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.Grey60

@Composable
fun TicketDetailScreen(
    modifier: Modifier,
) {
    var showEnterCodeDialog by remember { mutableStateOf(false) }
    var enterCodeError by remember { mutableStateOf(false) }
    var enterCode by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.Center)
                .clickable { showEnterCodeDialog = true },
            text = "입장 코드 입력하기",
            style = MaterialTheme.typography.bodySmall,
            color = Grey50,
            textDecoration = TextDecoration.Underline,
        )
    }

    if (showEnterCodeDialog) {
        BTDialog(
            onDismiss = {
                enterCodeError = false
                enterCode = ""
                showEnterCodeDialog = false
            },
            onClickPositiveButton = { /* TODO 입장 코드 검증 */ }
        ) {
            Text(
                text = "입장 코드로 입장 확인",
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "입장 코드는 공연관리 > 입장 관리\n페이지에서 확인 가능합니다.",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 4.dp),
                style = MaterialTheme.typography.bodySmall,
                color = Grey50,
            )
            BTTextField(
                text = enterCode,
                placeholder = "입장 코드를 입력해 주세요",
                onValueChanged = {
                    enterCodeError = false
                    enterCode = it
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
                    onDone = { /* TODO 입장 코드 검증 */ }
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
            if (enterCodeError) {
                Text(
                    text = "올바른 입장 코드를 입력해 주세요",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
    }
}
