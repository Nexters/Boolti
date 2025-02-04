package com.nexters.boolti.presentation.screen.profileedit.sns

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexters.boolti.domain.model.Sns
import com.nexters.boolti.presentation.R
import com.nexters.boolti.presentation.component.BTDialog
import com.nexters.boolti.presentation.component.BTTextField
import com.nexters.boolti.presentation.component.BTTextFieldDefaults
import com.nexters.boolti.presentation.component.BtAppBar
import com.nexters.boolti.presentation.component.BtAppBarDefaults
import com.nexters.boolti.presentation.component.MainButton
import com.nexters.boolti.presentation.component.MainButtonDefaults
import com.nexters.boolti.presentation.component.SelectableIcon
import com.nexters.boolti.presentation.extension.centerToTop
import com.nexters.boolti.presentation.theme.BooltiTheme
import com.nexters.boolti.presentation.theme.Grey30
import com.nexters.boolti.presentation.theme.Grey90
import com.nexters.boolti.presentation.theme.marginHorizontal

@Composable
fun SnsEditScreen(
    modifier: Modifier = Modifier,
    onAddSns: (type: Sns.SnsType, username: String) -> Unit = { _, _ -> },
    onEditSns: (id: String, type: Sns.SnsType, username: String) -> Unit = { _, _, _ -> },
    onRemoveSns: (id: String) -> Unit,
    navigateBack: () -> Unit,
    viewModel: SnsEditViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SnsEditScreen(
        modifier = modifier,
        isEditMode = uiState.isEditMode,
        selectedSns = uiState.selectedSns,
        username = uiState.username,
        usernameHasError = uiState.usernameHasError,
        onChangeSns = viewModel::setSns,
        onChangeUsername = viewModel::setUsername,
        onRemoveSns = { uiState.snsId?.let(onRemoveSns) },
        onComplete = {
            if (uiState.isEditMode) {
                uiState.snsId?.let { id ->
                    onEditSns(
                        id,
                        uiState.selectedSns,
                        uiState.username,
                    )
                }
            } else {
                onAddSns(uiState.selectedSns, uiState.username)
            }
        },
        navigateBack = navigateBack,
    )
}

@Composable
private fun SnsEditScreen(
    modifier: Modifier = Modifier,
    isEditMode: Boolean = false,
    selectedSns: Sns.SnsType = Sns.SnsType.INSTAGRAM,
    username: String = "",
    usernameHasError: Boolean = false,
    onChangeSns: (Sns.SnsType) -> Unit = {},
    onChangeUsername: (String) -> Unit = {},
    onRemoveSns: () -> Unit = {},
    onComplete: () -> Unit = {},
    navigateBack: () -> Unit = {},
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            BtAppBar(
                title = if (isEditMode) {
                    stringResource(R.string.sns_edit)
                } else {
                    stringResource(R.string.sns_add)
                },
                navigateButtons = {
                    BtAppBarDefaults.AppBarIconButton(
                        iconRes = R.drawable.ic_arrow_back,
                        onClick = navigateBack,
                    )
                },
                actionButtons = {
                    BtAppBarDefaults.AppBarTextButton(
                        label = stringResource(R.string.complete),
                        enabled = username.isNotBlank() && !usernameHasError,
                        onClick = onComplete,
                    )
                },
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            Column(
                modifier = Modifier.padding(horizontal = marginHorizontal),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                // SNS
                Row(
                    modifier = Modifier.padding(top = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Label(stringResource(R.string.sns))
                    SelectableIcon(
                        selected = selectedSns == Sns.SnsType.INSTAGRAM,
                        iconRes = R.drawable.ic_logo_instagram,
                        onClick = { onChangeSns(Sns.SnsType.INSTAGRAM) },
                        contentDescription = stringResource(R.string.sns_select_instagram_description),
                    )
                    SelectableIcon(
                        modifier = Modifier.padding(start = 12.dp),
                        selected = selectedSns == Sns.SnsType.YOUTUBE,
                        iconRes = R.drawable.ic_logo_youtube,
                        onClick = { onChangeSns(Sns.SnsType.YOUTUBE) },
                        contentDescription = stringResource(R.string.sns_select_youtube_description),
                    )
                }

                // Username
                Row(
                    modifier = Modifier.padding(bottom = 20.dp),
                ) {
                    Label(
                        label = stringResource(R.string.username),
                        modifier = Modifier.centerToTop(top = 24.dp),
                    )
                    BTTextField(
                        modifier = Modifier.weight(1f),
                        text = username,
                        isError = usernameHasError,
                        placeholder = stringResource(R.string.sns_username_placeholder),
                        supportingText = when {
                            username.contains('@') -> stringResource(R.string.sns_username_contains_at_error)
                            usernameHasError -> stringResource(R.string.contains_unsupported_char_error)
                            else -> null
                        },
                        trailingIcon = if (username.isNotEmpty()) {
                            { BTTextFieldDefaults.ClearButton(onClick = { onChangeUsername("") }) }
                        } else {
                            null
                        },
                        singleLine = true,
                        onValueChanged = onChangeUsername,
                    )
                }

                Spacer(Modifier.weight(1f))
                if (isEditMode) {
                    MainButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp),
                        colors = MainButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            contentColor = Grey90,
                        ),
                        label = stringResource(R.string.sns_delete),
                        onClick = { showDeleteDialog = true },
                    )
                }
            }

            if (showDeleteDialog) {
                BTDialog(
                    onDismiss = { showDeleteDialog = false },
                    positiveButtonLabel = stringResource(R.string.btn_delete),
                    onClickNegativeButton = { showDeleteDialog = false },
                    onClickPositiveButton = {
                        showDeleteDialog = false
                        onRemoveSns()
                    },
                ) {
                    Text(
                        text = stringResource(R.string.sns_delete_message),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun Label(
    label: String,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier
            .defaultMinSize(minWidth = 72.dp)
            .padding(end = 12.dp),
        text = label,
        color = Grey30,
        style = MaterialTheme.typography.bodySmall,
    )
}

@Preview
@Composable
private fun SnsEditPreview() {
    var selectedSns by remember { mutableStateOf(Sns.SnsType.INSTAGRAM) }
    var username by remember { mutableStateOf("") }
    val usernameHasError: Boolean = when (selectedSns) {
        Sns.SnsType.INSTAGRAM -> username.contains(Regex("[^0-9a-zA-Zㄱ-ㅎㅏ-ㅣ가-힣._]+"))
        Sns.SnsType.YOUTUBE -> username.contains(Regex("[^0-9a-zA-Zㄱ-ㅎㅏ-ㅣ가-힣._-]+"))
    }

    BooltiTheme {
        SnsEditScreen(
            isEditMode = true,
            selectedSns = selectedSns,
            username = username,
            usernameHasError = usernameHasError,
            onChangeSns = { selectedSns = it },
            onChangeUsername = { username = it },
        )
    }
}
