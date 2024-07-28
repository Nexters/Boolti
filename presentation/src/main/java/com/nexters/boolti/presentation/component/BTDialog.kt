package com.nexters.boolti.presentation.component

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.Grey05
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
