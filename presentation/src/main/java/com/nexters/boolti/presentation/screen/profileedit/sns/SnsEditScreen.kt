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
import androidx.compose.foundation.layout.size
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
                title = if (isEditMode) "SNS 편집" else "SNS 추가",
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
                modifier = Modifier.padding(marginHorizontal),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                InfoRow(
                    label = "SNS",
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        SelectableIcon(
                            selected = selectedSns == Sns.SnsType.INSTAGRAM,
                            iconRes = R.drawable.ic_logo_instagram,
                            onClick = { onChangeSns(Sns.SnsType.INSTAGRAM) },
                            contentDescription = "인스타그램 선택",
                        )
                        SelectableIcon(
                            modifier = Modifier.padding(start = 12.dp),
                            selected = selectedSns == Sns.SnsType.YOUTUBE,
                            iconRes = R.drawable.ic_logo_youtube,
                            onClick = { onChangeSns(Sns.SnsType.YOUTUBE) },
                            contentDescription = "유튜브 선택",
                        )
                    }
                }
                InfoRow(
                    label = "Username",
                ) {
                    BTTextField(
                        modifier = Modifier.weight(1f),
                        text = username,
                        isError = usernameHasError,
                        placeholder = "ex) boolti_official",
                        supportingText = when {
                            username.contains('@') -> "@을 제외환 Username을 입력해 주세요"
                            usernameHasError -> "지원하지 않는 특수문자가 포함되어 있습니다"
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
                        modifier = Modifier.fillMaxWidth(),
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
private fun InfoRow(
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.defaultMinSize(minWidth = 72.dp),
            text = label,
            color = Grey30,
            style = MaterialTheme.typography.bodySmall,
        )
        Spacer(Modifier.size(12.dp))
        content()
    }
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
            isEditMode = false,
            selectedSns = selectedSns,
            username = username,
            usernameHasError = usernameHasError,
            onChangeSns = { selectedSns = it },
            onChangeUsername = { username = it },
        )
    }
}
