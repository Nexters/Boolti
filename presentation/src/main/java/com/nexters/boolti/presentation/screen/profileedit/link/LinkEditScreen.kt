package com.nexters.boolti.presentation.screen.profileedit.link

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BTDialog
import com.nexters.boolti.presentation.component.BTTextField
import com.nexters.boolti.presentation.component.BtAppBar
import com.nexters.boolti.presentation.component.BtAppBarDefaults
import com.nexters.boolti.presentation.component.MainButton
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey90

@Composable
fun LinkEditScreen(
    onClickBack: () -> Unit = {},
    onClickComplete: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var showLinkRemoveDialog by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        modifier = modifier,
        topBar = {
            BtAppBar(
                navigateButtons = {
                    BtAppBarDefaults.AppBarIconButton(
                        onClick = onClickBack,
                        iconRes = R.drawable.ic_arrow_back,
                    )
                },
                actionButtons = {
                    BtAppBarDefaults.AppBarTextButton(
                        label = stringResource(R.string.complete),
                        onClick = onClickComplete,
                    )
                },
                title = stringResource(R.string.link_add),
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        modifier = Modifier.defaultMinSize(minWidth = 64.dp),
                        text = stringResource(R.string.link_name),
                        color = Grey30,
                    )
                    BTTextField(
                        modifier = Modifier.padding(start = 12.dp),
                        text = "",
                        placeholder = stringResource(R.string.link_name_placeholder),
                        onValueChanged = {},
                    )
                }
                Row(
                    modifier = Modifier.padding(top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        modifier = Modifier.defaultMinSize(minWidth = 64.dp),
                        text = stringResource(R.string.link_url),
                        color = Grey30,
                    )
                    BTTextField(
                        modifier = Modifier.padding(start = 12.dp),
                        text = "",
                        placeholder = stringResource(R.string.link_url_placeholder),
                        onValueChanged = {},
                    )
                }
            }
            MainButton(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                label = stringResource(R.string.link_remove),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    contentColor = Grey90,
                ),
            ) { }
        }

        showLinkRemoveDialog?.let { removeTargetIndex ->
            BTDialog(
                positiveButtonLabel = stringResource(R.string.btn_delete),
                negativeButtonLabel = stringResource(R.string.cancel),
                onClickPositiveButton = {},
                onClickNegativeButton = { showLinkRemoveDialog = null },
                onDismiss = { showLinkRemoveDialog = null },
            ) {
                Text(stringResource(R.string.remove_link_dialog_message))
            }
        }
    }
}
