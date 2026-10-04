package com.nexters.boolti.presentation.screen.debug.info

import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.MainButton
import com.nexters.boolti.presentation.component.MainButtonDefaults
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.Grey05
import com.nexters.boolti.presentation.theme.Grey15
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey80

@Composable
internal fun DebugInfoScreen(
    onDismiss: () -> Unit,
    viewModel: DebugInfoViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val copiedMessage = stringResource(R.string.debug_info_copied)

    DebugInfoScreen(
        uiState = uiState,
        onDismiss = onDismiss,
        onClickCopy = {
            clipboardManager.setText(AnnotatedString(uiState.text))
            // Android 13부터는 시스템이 복사 알림을 띄운다
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                Toast.makeText(context, copiedMessage, Toast.LENGTH_SHORT).show()
            }
        },
    )
}

@Composable
private fun DebugInfoScreen(
    uiState: DebugInfoUiState,
    onDismiss: () -> Unit,
    onClickCopy: () -> Unit,
) {
    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = onDismiss,
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceTint),
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = stringResource(R.string.debug_info_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = Grey15,
                )
                // 제목과 버튼은 고정하고 내용만 스크롤한다
                SelectionContainer(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .padding(vertical = 16.dp)
                        .verticalScroll(rememberScrollState()),
                ) {
                    Text(
                        text = uiState.text,
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace,
                        color = Grey30,
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MainButton(
                        modifier = Modifier.weight(1f),
                        label = stringResource(R.string.debug_info_close),
                        colors = MainButtonDefaults.buttonColors(
                            containerColor = Grey80,
                            contentColor = Grey05,
                        ),
                        onClick = onDismiss,
                    )
                    MainButton(
                        modifier = Modifier.weight(1f),
                        label = stringResource(R.string.debug_info_copy),
                        enabled = uiState.text.isNotEmpty(),
                        onClick = onClickCopy,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun DebugInfoScreenPreview() {
    BooltiTheme {
        DebugInfoScreen(
            uiState = DebugInfoUiState(text = "[앱]\n버전: 1.0.0-abc1234\n\n[유저]\n로그인 안 함"),
            onDismiss = {},
            onClickCopy = {},
        )
    }
}
