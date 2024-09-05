package com.nexters.boolti.presentation.screen.profileedit.link

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
    modifier: Modifier = Modifier,
    onAddLink: (name: String, url: String) -> Unit,
    onEditLink: (id: String, name: String, url: String) -> Unit,
    onRemoveLink: (id: String) -> Unit,
    navigateBack: () -> Unit = {},
    viewModel: LinkEditViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LinkEditScreen(
        modifier = modifier,
        isEditMode = uiState.isEditMode,
        linkName = uiState.linkName,
        linkUrl = uiState.url,
        onClickBack = navigateBack,
        onClickComplete = {
            if (uiState.isEditMode) {
                onEditLink(viewModel.editLinkId, uiState.linkName, uiState.url)
            } else {
                onAddLink(uiState.linkName, uiState.url)
            }
        },
        onChangeLinkName = viewModel::onChangeLinkName,
        onChangeLinkUrl = viewModel::onChangeLinkUrl,
        requireRemove = { if (viewModel.editLinkId.isNotBlank()) onRemoveLink(viewModel.editLinkId) },
    )
}

@Composable
fun LinkEditScreen(
    isEditMode: Boolean,
    linkName: String,
    linkUrl: String,
    onClickBack: () -> Unit,
    onClickComplete: () -> Unit,
    onChangeLinkName: (String) -> Unit,
    onChangeLinkUrl: (String) -> Unit,
    requireRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showLinkRemoveDialog by remember { mutableStateOf(false) }

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
                        enabled = linkName.isNotBlank() && linkUrl.isNotBlank(),
                    )
                },
                title = stringResource(if (isEditMode) R.string.link_edit else R.string.link_add),
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
                        text = linkName,
                        placeholder = stringResource(R.string.link_name_placeholder),
                        singleLine = true,
                        onValueChanged = onChangeLinkName,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next,
                        ),
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
                        text = linkUrl,
                        placeholder = stringResource(R.string.link_url_placeholder),
                        singleLine = true,
                        onValueChanged = onChangeLinkUrl,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Uri,
                            imeAction = ImeAction.Default,
                        ),
                    )
                }
            }
            if (isEditMode) {
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
                    onClick = { showLinkRemoveDialog = true },
                )
            }
        }

        if (showLinkRemoveDialog) {
            BTDialog(
                positiveButtonLabel = stringResource(R.string.btn_delete),
                negativeButtonLabel = stringResource(R.string.cancel),
                onClickPositiveButton = {
                    requireRemove()
                    showLinkRemoveDialog = false
                },
                onClickNegativeButton = { showLinkRemoveDialog = false },
                onDismiss = { showLinkRemoveDialog = false },
            ) {
                Text(stringResource(R.string.remove_link_dialog_message))
            }
        }
    }
}