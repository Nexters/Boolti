package com.nexters.boolti.presentation.screen.profileedit.link

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BTDialog
import com.nexters.boolti.presentation.component.BTTextField
import com.nexters.boolti.presentation.component.BTTextFieldDefaults
import com.nexters.boolti.presentation.component.BtAppBar
import com.nexters.boolti.presentation.component.BtAppBarDefaults
import com.nexters.boolti.presentation.component.MainButton
import com.nexters.boolti.presentation.screen.link.LinkEditEvent
import com.nexters.boolti.presentation.screen.link.LinkListViewModel
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey90
import com.nexters.boolti.presentation.theme.marginHorizontal
import com.nexters.boolti.presentation.util.ObserveAsEvents

@Composable
fun LinkEditScreen(
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LinkListViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val event = viewModel.linkEditEvent

    ObserveAsEvents(event) {
        when (it) {
            LinkEditEvent.Finish -> navigateUp()
        }
    }

    LinkEditScreen(
        isEditMode = uiState.editingLink?.id?.isNotEmpty() == true,
        linkName = uiState.editingLink?.name.orEmpty(),
        linkUrl = uiState.editingLink?.url.orEmpty(),
        onClickBack = navigateUp,
        onClickComplete = viewModel::completeAddOrEditLink,
        onChangeLinkName = viewModel::onLinkNameChanged,
        onChangeLinkUrl = viewModel::onLinkUrlChanged,
        requireRemove = viewModel::removeLink,
        modifier = modifier,
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
    val linkNameInteractionSource = remember { MutableInteractionSource() }
    val linkUrlInteractionSource = remember { MutableInteractionSource() }
    val linkNameFocused by linkNameInteractionSource.collectIsFocusedAsState()
    val linkUrlFocused by linkUrlInteractionSource.collectIsFocusedAsState()

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
                modifier = Modifier.padding(horizontal = 20.dp),
            ) {
                Row(
                    modifier = Modifier.padding(top = 20.dp),
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
                        trailingIcon = if (linkNameFocused && linkName.isNotEmpty()) {
                            { BTTextFieldDefaults.ClearButton(onClick = { onChangeLinkName("") }) }
                        } else {
                            null
                        },
                        interactionSource = linkNameInteractionSource,
                    )
                }
                Row(
                    modifier = Modifier.padding(top = 16.dp, bottom = 20.dp),
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
                        trailingIcon = if (linkUrlFocused && linkUrl.isNotEmpty()) {
                            { BTTextFieldDefaults.ClearButton(onClick = { onChangeLinkUrl("") }) }
                        } else {
                            null
                        },
                        interactionSource = linkUrlInteractionSource,
                    )
                }
            }
            if (isEditMode) {
                MainButton(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = marginHorizontal)
                        .padding(bottom = 20.dp)
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

@Preview
@Composable
private fun LinkEditScreenPreview() {
    BooltiTheme {
        LinkEditScreen(
            isEditMode = true,
            linkName = "링크 이름",
            linkUrl = "링크 URL",
            onClickBack = {},
            onClickComplete = {},
            onChangeLinkName = {},
            onChangeLinkUrl = {},
            requireRemove = {},
        )
    }
}
