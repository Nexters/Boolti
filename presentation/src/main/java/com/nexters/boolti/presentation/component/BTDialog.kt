package com.nexters.boolti.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.Error
import com.nexters.boolti.presentation.theme.Grey05
import com.nexters.boolti.presentation.theme.Grey15
import com.nexters.boolti.presentation.theme.Grey50
import com.nexters.boolti.presentation.theme.Grey80

@Composable
fun BTDialog(
    modifier: Modifier = Modifier,
    enableDismiss: Boolean = true,
    showCloseButton: Boolean = true,
    onDismiss: () -> Unit = {},
    negativeButtonLabel: String = stringResource(R.string.cancel),
    onClickNegativeButton: (() -> Unit)? = null,
    positiveButtonLabel: String = stringResource(R.string.btn_ok),
    positiveButtonEnabled: Boolean = true,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    onClickPositiveButton: () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    Dialog(
        properties = DialogProperties(
            dismissOnBackPress = enableDismiss,
            dismissOnClickOutside = enableDismiss,
            usePlatformDefaultWidth = false,
        ),
        onDismissRequest = { onDismiss() }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceTint),
        ) {
            Column(
                modifier = modifier
                    .padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
                horizontalAlignment = horizontalAlignment,
            ) {
                Box(
                    modifier = Modifier
                        .height(if (showCloseButton) 48.dp else 32.dp)
                        .fillMaxWidth()
                ) {
                    if (showCloseButton) {
                        Icon(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .clickable { onDismiss() },
                            painter = painterResource(id = R.drawable.ic_close),
                            tint = Grey50,
                            contentDescription = stringResource(R.string.description_close_button),
                        )
                    }
                }
                content()
                Spacer(modifier = Modifier.size(28.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (onClickNegativeButton != null) {
                        MainButton(
                            modifier = Modifier.weight(1f),
                            label = negativeButtonLabel,
                            onClick = onClickNegativeButton,
                            colors = MainButtonDefaults.buttonColors(
                                containerColor = Grey80,
                                contentColor = Grey05,
                            )
                        )
                    }
                    MainButton(
                        modifier = Modifier.weight(1f),
                        label = positiveButtonLabel,
                        enabled = positiveButtonEnabled,
                        onClick = onClickPositiveButton,
                    )
                }
            }
        }
    }
}

@Composable
fun NoticeDialog(
    title: String,
    content: String,
    emphasizedText: String = "",
    onDismiss: () -> Unit,
) {
    BTDialog(
        onDismiss = onDismiss,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = title,
                color = Grey15,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
            )
            if (emphasizedText.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .clip(shape = RoundedCornerShape(4.dp))
                        .background(color = Grey80)
                        .padding(16.dp)
                ) {
                    Text(
                        text = emphasizedText,
                        color = Error,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
            Text(
                modifier = Modifier.padding(
                    top = if (emphasizedText.isNotBlank()) 16.dp else 20.dp
                ),
                text = content,
                style = MaterialTheme.typography.bodySmall,
                color = Grey50,
                textAlign = TextAlign.Center,
            )
        }
    }
}


@Preview
@Composable
fun BTDialogPreview() {
    BooltiTheme {
        Surface {
            BTDialog {
                Text(text = "관리자 코드로 입장 확인")
            }
        }
    }
}

@Preview
@Composable
fun BTDialogHavingNegativeButtonPreview() {
    BooltiTheme {
        Surface {
            BTDialog(
                onClickNegativeButton = {}
            ) {
                Text(text = "관리자 코드로 입장 확인")
            }
        }
    }
}

@Preview
@Composable
fun NoticeDialogPreview() {
    BooltiTheme {
        Surface {
            NoticeDialog(
                title = "맴뱀페이 결제 불가 안내",
                content = "이용에 불편을 드려 죄송합니다. 서비스가 정상화되는 즉시 다시 안내드릴 예정이오니, 다른 결제수단을 이용해 주시면 감사하겠습니다.",
                onDismiss = {}
            )
        }
    }
}

@Preview
@Composable
fun NoticeEmphasizedDialogPreview() {
    BooltiTheme {
        Surface {
            NoticeDialog(
                title = "맴뱀페이 결제 불가 안내",
                content = "이용에 불편을 드려 죄송합니다. 서비스가 정상화되는 즉시 다시 안내드릴 예정이오니, 다른 결제수단을 이용해 주시면 감사하겠습니다.",
                emphasizedText = "현재 야근 중인 관계로 코딩이 영구적으로 불가합니다.",
                onDismiss = {}
            )
        }
    }
}