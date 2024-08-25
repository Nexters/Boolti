package com.nexters.boolti.presentation.screen.profileedit.link

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BTDialog

@Composable
fun LinkEditScreen(
    modifier: Modifier = Modifier,
) {
    var showLinkRemoveDialog by remember { mutableStateOf<Int?>(null) }

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
